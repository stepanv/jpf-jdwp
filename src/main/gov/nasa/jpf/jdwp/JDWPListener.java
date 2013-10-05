/* 
   Copyright (C) 2013 Stepan Vavra

This file is part of (Java Debug Wire Protocol) JDWP for 
Java PathFinder (JPF) project.

JDWP for JPF is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

JDWP for JPF is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 
 */

package gov.nasa.jpf.jdwp;

import gnu.classpath.jdwp.Jdwp;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jdwp.event.BreakpointEvent;
import gov.nasa.jpf.jdwp.event.ClassPrepareEvent;
import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.EventBase.EventKind;
import gov.nasa.jpf.jdwp.event.EventRequestManager;
import gov.nasa.jpf.jdwp.event.ExceptionEvent;
import gov.nasa.jpf.jdwp.event.MethodEntryEvent;
import gov.nasa.jpf.jdwp.event.MethodExitEvent;
import gov.nasa.jpf.jdwp.event.MethodExitWithReturnValueEvent;
import gov.nasa.jpf.jdwp.event.SingleStepEvent;
import gov.nasa.jpf.jdwp.event.ThreadDeathEvent;
import gov.nasa.jpf.jdwp.event.ThreadStartEvent;
import gov.nasa.jpf.jdwp.exception.special.NoPropertyViolationException;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.jdwp.util.FieldVisitor;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.jdwp.value.ValueUtils;
import gov.nasa.jpf.jvm.bytecode.FieldInstruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassInfoException;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.ClassParseException;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ExceptionHandler;
import gov.nasa.jpf.vm.HandlerContext;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadInfo.State;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.VMListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This is the only one point how the JPF communicates with the JDWP backend.<br/>
 * It is a common design that all the JPF extensions (such as this
 * <i>jpf-jdwp</i> project) use only the <i>Listener API</i>.
 * </p>
 * <p>
 * The flow of the program execution is delegated from the listener methods in
 * this class to the JDWP back-end. Even though there are still other JDWP
 * threads that can theoretically communicate with JPF but this is disallowed
 * since JPF may not be in a consistent state and the information that may be
 * obtained would be corrupted.
 * </p>
 * 
 * @see JDWPRunner
 * @author stepan
 * 
 */
public class JDWPListener extends JDWPSearchBase implements VMListener {

  @Override
  public void objectReleased(VM vm, ThreadInfo currentThread, ElementInfo releasedObject) {
    logger.debug("Object released: {} .. ID: {}", releasedObject, releasedObject.getObjectRef());
  }

  private VirtualMachine virtualMachine;
  private FieldVisitor fieldVisitor = new FieldVisitor();

  private boolean runningAsListenerOnly = false;
  // this is here TEMPORARILLY until we solve the JDWP singleton problem
  private Jdwp jdwp;

  /**
   * <p>
   * This constructor is used by the JPF if this JDWP back-end is enabled in the
   * <i>listener only</i> mode. (ie. The program is not executed from the
   * {@link JDWPRunner#main(String[])} method.)
   * </p>
   * <p>
   * To enable the JDWP back-end in such mode just enable this listener provided
   * all the required classes are available on the <i>JPF native classpath</i>.<br/>
   * (see <tt>jpf-core.native_classpath</tt>)
   * </p>
   */
  public JDWPListener() {
    runningAsListenerOnly = true;

    this.virtualMachine = new VirtualMachine(VM.getVM().getJPF(), Thread.currentThread());

    jdwp = new Jdwp(virtualMachine);

    String jdwpProperty = VM.getVM().getConfig().getString("jpf-jdwp.jdwp");

    if (jdwpProperty == null) {
      jdwpProperty = System.getProperty("jdwp");
    }

    if (jdwpProperty == null) {
      throw new IllegalStateException("Missing jdwp configuration!");
    }

    jdwp.configure(jdwpProperty);
    virtualMachine.setJdwp(jdwp);
    jdwp.start();

    while (Jdwp.suspendOnStartup() || !jdwp.isServer()) {
      try {
        jdwp.join();
        break;
      } catch (InterruptedException e) {
      }
    }

    // Get the lock before the JPF starts.
    // This lock is paired with unlock {@link
    // JDWPListener#searchFinished(Search)}
    virtualMachine.getRunLock().lock();
  }

  /**
   * The standard way to initialize this listener.
   * 
   * @param jpf
   *          The JPF.
   * @param virtualMachine
   *          The JDWP VM representation.
   */
  public JDWPListener(JPF jpf, VirtualMachine virtualMachine) {
    this.virtualMachine = virtualMachine;
  }

  @Override
  public void methodEntered(VM vm, ThreadInfo currentThread, MethodInfo enteredMethod) {
    if (hasNonnullEventRequests(EventKind.METHOD_ENTRY)) {
      Instruction instruction = enteredMethod.getInstruction(0);
      if (instruction.getMethodInfo() != null && instruction.getMethodInfo().getClassInfo() != null) {
        MethodEntryEvent methodEntryEvent = new MethodEntryEvent(currentThread, Location.factory(instruction));
        dispatchEvent(methodEntryEvent);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nasa.jpf.jdwp.JDWPListenerBase#methodExited(gov.nasa.jpf.vm.VM,
   * gov.nasa.jpf.vm.ThreadInfo, gov.nasa.jpf.vm.MethodInfo)
   */
  @Override
  public void methodExited(VM vm, ThreadInfo currentThread, MethodInfo exitedMethod) {
    if (hasNonnullEventRequests(EventKind.METHOD_EXIT, EventKind.METHOD_EXIT_WITH_RETURN_VALUE)) {
      if (currentThread.getPendingException() != null) {
        // according to the specification, if exception is thrown, no
        // method exit events are generated
        return;
      }
      Instruction instruction = exitedMethod.getInstruction(0);
      if (instruction.getMethodInfo() != null && instruction.getMethodInfo().getClassInfo() != null) {
        Event methodExitEvent;

        // The specification isn't clear whether these two events are
        // exclusive or not and thus we let decide the debugger which
        // ones are requested.

        if (hasNonnullEventRequests(EventKind.METHOD_EXIT_WITH_RETURN_VALUE)) {
          Value returnValue = ValueUtils.methodReturnValue(exitedMethod, currentThread.getTopFrame());
          methodExitEvent = new MethodExitWithReturnValueEvent(currentThread, Location.factory(instruction), returnValue);
          dispatchEvent(methodExitEvent);
        }

        if (hasNonnullEventRequests(EventKind.METHOD_EXIT)) {
          methodExitEvent = new MethodExitEvent(currentThread, Location.factory(instruction));
          dispatchEvent(methodExitEvent);
        }
      }
    }
  }

  /**
   * Thread start handler. <br/>
   * Thread starts work without any problems.
   * 
   * @see JDWPListener#threadTerminated(VM, ThreadInfo)
   */
  @Override
  public void threadStarted(VM vm, ThreadInfo startedThread) {
    if (hasNonnullEventRequests(EventKind.THREAD_START)) {
      // TODO Waiting for fix in JPF - already submitted to Peter
      startedThread.setState(State.RUNNING);
      lastKnownThreadStates.put(startedThread.getThreadObjectRef(), State.RUNNING);
      logger.info("Started thread: " + startedThread);

      ThreadStartEvent threadStartEvent = new ThreadStartEvent(startedThread);
      dispatchEvent(threadStartEvent);
    }
  }

  /**
   * Thread termination handler.<br/>
   * Note that there is not 1:1 relation between thread terminates and starts if
   * states are traversed by JPF. Because of that a custom behavior needs to be
   * implemented if not debugging a single trace.<br/>
   * <h2>Eclipse debugger</h2>
   * <ul>
   * <li>Thread starts are ok - doesn't matter how many times a thread start is
   * received (unless a race occurs which happens with Eclipse Juno for
   * instance). In that case a thread can be shown multiple times in the Debug
   * view.</li>
   * <li>Thread deaths are so not ok. Eclipse implements deferred thread deaths
   * handling if a thread is not known which makes thread disappear right after
   * it is created and thread start event is received. Needs to be investigated
   * more ... seems to be very tricky.<br/>
   * As a workaround for those weird deferred deaths handling, we're always
   * sending thread start event right before thread death event is sent - it
   * helps but doesn't avoid all the problems.</li>
   * </ul>
   * 
   * @see JDWPListener#threadStarted(VM, ThreadInfo)
   * 
   */
  @Override
  public void threadTerminated(VM vm, ThreadInfo terminatedThread) {
    if (hasNonnullEventRequests(EventKind.THREAD_DEATH)) {
      // this is the workaround for Eclipse and it's deferred thread
      // deaths handling
      ThreadStartEvent ts = new ThreadStartEvent(terminatedThread);
      dispatchEvent(ts);

      lastKnownThreadStates.put(terminatedThread.getThreadObjectRef(), State.TERMINATED);
      logger.debug("Thread terminated: {}", terminatedThread);

      ThreadDeathEvent td = new ThreadDeathEvent(terminatedThread);
      dispatchEvent(td);
    }
  }

  /**
   * This list is populated with all the classes that were loaded during the JPF
   * startup prior the time it was allowed to start sending events because no
   * such events were registered.
   */
  List<ClassInfo> postponedLoadedClasses = new ArrayList<ClassInfo>();

  @Override
  public void classLoaded(VM vm, ClassInfo loadedClass) {
    virtualMachine.notifyClassLoaded(loadedClass);
    // [for PJA] This is weird.. According to JDWP we should sent
    // threadID where this class loaded event occurred
    // but in case of JPF it doesn't have a system thread
    // (which caused class load before the main thread was executed) .. does
    // it?
    if (vm.getCurrentThread() != null && vm.isInitialized()) {
      logger.debug("Class loaded: {}", loadedClass);
      ClassPrepareEvent classPrepareEvent = new ClassPrepareEvent(vm.getCurrentThread(), loadedClass);
      dispatchEvent(classPrepareEvent);
    } else {
      logger.info("Not notifying about class load: {}", loadedClass);
      postponedLoadedClasses.add(loadedClass);
    }
  }

  final static Logger logger = LoggerFactory.getLogger(JDWPListener.class);

  @Override
  public void instructionExecuted(VM vm, ThreadInfo currentThread, Instruction nextInstruction, Instruction executedInstruction) {
    // just to let the other thread run the jdwp commands
    virtualMachine.getExecutionManager().executionHook();
  }

  @Override
  public void vmInitialized(VM vm) {
    virtualMachine.startHook(vm, postponedLoadedClasses);

    // we also need to send thread start event which is not generated
    // by design by JPF
    // TODO consult this again so that it doesn't have to be workarounded
    ThreadStartEvent threadStartEvent = new ThreadStartEvent(vm.getCurrentThread());
    lastKnownThreadStates.put(vm.getCurrentThread().getThreadObjectRef(), State.RUNNING);
    dispatchEvent(threadStartEvent);
  }

  private List<Event> addAndConditionallyInit(Event event, List<Event> events) {
    if (events == null) {
      events = new LinkedList<>();
    }
    events.add(event);
    return events;
  }

  @Override
  public void executeInstruction(VM vm, ThreadInfo currentThread, Instruction instructionToExecute) {

    virtualMachine.getExecutionManager().executionHook();

    if (instructionToExecute.getMethodInfo() != null && instructionToExecute.getMethodInfo().getClassInfo() != null) {

      List<Event> locationGroupEvents = null;

      if (logger.isTraceEnabled()) {
        if (instructionToExecute instanceof InvokeInstruction) {
          // This requires InvokeInstruction#arguments to be
          // public .. since this is just for debugging it has to be
          // removed
          // logger.trace("Instruction: '{}' args: {} line: {}",
          // instructionToExecute, ((InvokeInstruction)
          // instructionToExecute).arguments,
          // instructionToExecute.getFileLocation());
        } else {
          logger.trace("Instruction: '{}' line: {}", instructionToExecute, instructionToExecute.getFileLocation());
        }
      }
      Location locationOfInstructionToExecute = Location.factory(instructionToExecute);

      if (hasNonnullEventRequests(EventKind.BREAKPOINT)) {
        BreakpointEvent breakpointEvent = new BreakpointEvent(currentThread, locationOfInstructionToExecute);
        locationGroupEvents = addAndConditionallyInit(breakpointEvent, locationGroupEvents);
      }

      if (hasNonnullEventRequests(EventKind.FIELD_ACCESS, EventKind.FIELD_MODIFICATION) && instructionToExecute instanceof FieldInstruction) {
        fieldVisitor.initalize(currentThread);
        ((FieldInstruction) instructionToExecute).accept(fieldVisitor);
      }

      if (hasNonnullEventRequests(EventKind.SINGLE_STEP)) {
        SingleStepEvent singleStepEvent = new SingleStepEvent(currentThread, locationOfInstructionToExecute);
        locationGroupEvents = addAndConditionallyInit(singleStepEvent, locationGroupEvents);
      }

      dispatchEvent(locationGroupEvents);
    }
  }

  /**
   * Whether the {@link EventRequestManager} has registered more than 0 event
   * requests for the given event kind.
   * 
   * @param eventKind
   *          The event kind.
   * @return true or false
   */
  private boolean hasNonnullEventRequests(EventKind eventKind) {
    return Jdwp.getEventRequestManager().eventRequestCount(eventKind) > 0;
  }

  /**
   * Whether the {@link EventRequestManager} has registered more than 0 event
   * request for at least one of the given event kinds.
   * 
   * @param firstEventKind
   *          The first event kind.
   * @param secondEventKind
   *          The second event kind.
   * @return true or false
   */
  private boolean hasNonnullEventRequests(EventKind firstEventKind, EventKind secondEventKind) {
    return hasNonnullEventRequests(firstEventKind) || hasNonnullEventRequests(secondEventKind);
  }

  /**
   * The hander for uncaught exceptions.
   * 
   * @param vm
   *          The VM.
   * @param currentThread
   *          The thread that generated the exception throw.
   * @param thrownException
   *          The exception to be thrown.
   */
  private void uncaughtExceptionThrown(VM vm, ThreadInfo currentThread, ElementInfo thrownException) {
    Instruction instruction = vm.getInstruction();
    if (instruction != null) {
      ExceptionEvent exceptionEvent = new ExceptionEvent(currentThread, Location.factorySafe(instruction, currentThread), thrownException,
          null);
      dispatchEvent(exceptionEvent);
    }
  }

  /**
   * The hander for caught exceptions.
   * 
   * @param vm
   *          The VM.
   * @param currentThread
   *          The thread that generated the exception throw.
   * @param thrownException
   *          The exception to be thrown.
   * @param handlerFrame
   *          The frame where this exception will be handled.
   * @param matchingHandler
   *          The representation of the handler that will handle this exception.
   *          (At the handler frame if anyone is interested.)
   */
  private void caughtExceptionThrown(VM vm, ThreadInfo currentThread, ElementInfo thrownException, StackFrame handlerFrame,
                                     ExceptionHandler matchingHandler) {
    Instruction instruction = vm.getInstruction();
    MethodInfo handlerMethodInfo = handlerFrame.getMethodInfo();
    int handlerInstructionIndex = matchingHandler.getHandler();

    Instruction catchInstruction = handlerMethodInfo.getInstructionAt(handlerInstructionIndex);

    if (instruction != null && catchInstruction != null) {
      ExceptionEvent exceptionEvent = new ExceptionEvent(currentThread, Location.factorySafe(instruction, currentThread), thrownException,
          Location.factorySafe(catchInstruction, currentThread));
      dispatchEvent(exceptionEvent);
    } else {
      // We don't end here but for the sake of completeness...
      uncaughtExceptionThrown(vm, currentThread, thrownException);
    }
  }

  @Override
  public void exceptionThrown(VM vm, ThreadInfo currentThread, ElementInfo thrownException) {
    logger.trace("Exception thrown: {}", thrownException);

    HandlerContext handlerContext = currentThread.getHandlerContextFor(thrownException.getClassInfo());

    if (handlerContext != null) {
      caughtExceptionThrown(vm, currentThread, thrownException, handlerContext.getFrame(), handlerContext.getHandler());
    } else {
      uncaughtExceptionThrown(vm, currentThread, thrownException);
    }
  }

  /**
   * A helper method for dispatching events.<br/>
   * Note that this is not the only point where events are dispatched.
   * 
   * @param event
   *          The event.
   */
  private void dispatchEvent(Event event) {
    Jdwp.notify(event);
  }

  /**
   * Dispatch the group of events
   * 
   * @param events
   *          or null (null means no-op)
   */
  private void dispatchEvent(List<Event> events) {
    if (events != null) {
      Jdwp.notify(events);
    }
  }

  Map<Integer, State> lastKnownThreadStates = new HashMap<Integer, ThreadInfo.State>();

  /**
   * This is a way how the thread mess is put in order.<br/>
   * This is because of Eclipse bad implementation of incoming thread event
   * notifications which is full of race conditions.
   */
  public void fixThreadNotificationState() {
    for (ThreadInfo threadInfo : VM.getVM().getThreadList().getThreads()) {

      int threadObjectRef = threadInfo.getThreadObjectRef();
      State lastKnownThreadState = lastKnownThreadStates.get(threadObjectRef);

      if (lastKnownThreadState == null) {
        // the debugger doesn't know the thread yet
        continue;
      }

      if (threadInfo.isTerminated() && lastKnownThreadState != State.TERMINATED) {
        logger.debug("Thread info isa already dead or new: {}", threadInfo);

        // again as a workaround we're sending thread start event right
        // before the thread death event to avoid deferred thread deaths
        // handling
        ThreadStartEvent ts = new ThreadStartEvent(threadInfo);
        dispatchEvent(ts);

        ThreadDeathEvent threadDeathEvent = new ThreadDeathEvent(threadInfo);
        dispatchEvent(threadDeathEvent);

        lastKnownThreadStates.put(threadObjectRef, threadInfo.getState());

      } else if (!threadInfo.isTerminated() && lastKnownThreadState == State.TERMINATED) {
        logger.debug("Thread info is back alive: {}", threadInfo);

        ThreadStartEvent threadStartEvent = new ThreadStartEvent(threadInfo);
        dispatchEvent(threadStartEvent);

        lastKnownThreadStates.put(threadObjectRef, threadInfo.getState());

      }

    }

  }

  @Override
  public void stateBacktracked(Search search) {
    logger.trace("State backtracked: {}");
    fixThreadNotificationState();
  }

  @Override
  public void stateAdvanced(Search search) {
    logger.trace("State advanced: {}", search);
    fixThreadNotificationState();
  }

  @Override
  public void stateProcessed(Search search) {
    logger.trace("State processed: {}", search);
    fixThreadNotificationState();
  }

  @Override
  public void statePurged(Search search) {
    logger.trace("State purged: {}", search);
    fixThreadNotificationState();
  }

  @Override
  public void stateStored(Search search) {
    logger.trace("State stored: {}", search);
    fixThreadNotificationState();
  }

  @Override
  public void stateRestored(Search search) {
    logger.trace("Processing search");
    fixThreadNotificationState();
  }

  @Override
  public void searchStarted(Search search) {
    logger.trace("Processing search");
    fixThreadNotificationState();
  }

  @Override
  public void searchFinished(Search search) {
    fixThreadNotificationState();
    if (runningAsListenerOnly) {
      jdwp.shutdown();

      // the unlock here is just for packet processor to be able to finish
      // if the lock is owned
      // We cannot be sure whether the lock is owned or not since the
      // Exception could have been thrown from both locked and unlocked
      // sections
      virtualMachine.getRunLock().unlockIfOwned();
    }
  }

  @Override
  public void propertyViolated(Search search) {
    Instruction instruction = search.getVM().getInstruction();
    ThreadInfo currentThread = ThreadInfo.getCurrentThread();

    // load and notify about the superclass of the NoPropertyViolationException
    // class
    ClassInfo throwableClass = ClassLoaderInfo.getSystemResolvedClassInfo(Throwable.class.getName());
    ClassPrepareEvent classPrepareEvent = new ClassPrepareEvent(currentThread, throwableClass);
    dispatchEvent(classPrepareEvent);

    try (InputStream is = NoPropertyViolationException.class.getResourceAsStream(NoPropertyViolationException.class.getSimpleName()
        + ".class");) {
      
      // load and notify about the NoPropertyViolationException class
      byte[] data = readData(is);
      ClassInfo propertyViolatedClass = ClassLoaderInfo.getCurrentClassLoader()
          .getResolvedClassInfo(NoPropertyViolationException.class.getName(), data, 0, data.length);

      classPrepareEvent = new ClassPrepareEvent(currentThread, propertyViolatedClass);

      // as a callback for the dispatch of this event an exception request will
      // be created
      dispatchEvent(classPrepareEvent);

      // now, there should be a request matching the
      // NoPropertyViolationException exception throw

      if (instruction != null) {

        // print out the banner
        System.out
            .println("====================================================== JPF JDWP Stopped the execution due to a property violation.");
        System.out.println(search.getCurrentError().getDescription());
        System.out.println(search.getCurrentError().getDetails());

        // create an exception instance so that the throw can be simulated and
        // reported back to the debugger
        ElementInfo ei = VM.getVM().getHeap().newObject(propertyViolatedClass, currentThread);
        ExceptionEvent exceptionEvent = new ExceptionEvent(currentThread, Location.factorySafe(instruction, currentThread), ei, null);
        dispatchEvent(exceptionEvent);

      }
    } catch (ClassInfoException | ClassParseException | IOException e) {
      logger.error("An error occurred during a property violation notification.", e);
    }
  }

  /**
   * Reads the data from the input stream.
   * 
   * @param is
   *          The input stream to read from.
   * @return
   * @throws ClassParseException
   * @throws IOException
   */
  private static byte[] readData(InputStream is) throws ClassParseException, IOException {
    byte[] data = new byte[5 * 1024];
    int nRead = 0;

    while (nRead < data.length) {
      int n = is.read(data, nRead, (data.length - nRead));
      if (n < 0) {
        return Arrays.copyOf(data, nRead);
      }
      nRead += n;
    }

    throw new ClassParseException("Prepared buffer too short.");
  }

}
