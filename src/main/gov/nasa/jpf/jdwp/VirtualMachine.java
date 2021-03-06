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
import gov.nasa.jpf.JPF.ExitException;
import gov.nasa.jpf.jdwp.event.ClassPrepareEvent;
import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.EventRequest;
import gov.nasa.jpf.jdwp.event.VmDeathEvent;
import gov.nasa.jpf.jdwp.event.VmStartEvent;
import gov.nasa.jpf.jdwp.exception.id.object.InvalidObjectException;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.util.SafeLock;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadInfo.State;
import gov.nasa.jpf.vm.VM;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * The {@link VirtualMachine} class represents the VM for the JDWP back-end.
 * That effectively means, all the VM related information JDWP needs to manage
 * is down here.
 * </p>
 * <p>
 * <h3>Synchronization</h3>
 * It's actually not clear how events and related consecutive debugger actions
 * are synchronized.<br/>
 * At first, a following problem needs to be solved:<br/>
 * <ol>
 * <li>JDWP agent sends an event (a conditional breakpoint for instance (note
 * that the condition is always evaluated at the debugger side, thus a big
 * number of unused breakpoint events is sent))</li>
 * <li>The breakpoint has suspend thread related action</li>
 * <li>The debugger decides that breakpoint condition is not satisfied and sends
 * back resume</li>
 * <li>The JDWP receive resume command.</li>
 * <li>And now the suspend command (related to the breakpoint event) is
 * performed.</li>
 * <li>Deadlock</li>
 * </ol>
 * <h4>Solution</h4>
 * Event send and the related suspend action has to be encapsulated as a atomic
 * action.
 * 
 * </p>
 * 
 * @author stepan
 * 
 */
public class VirtualMachine {
  private JPF jpf;
  private List<ClassInfo> loadedClases = new CopyOnWriteArrayList<ClassInfo>();
  private boolean started;

  static final Logger logger = LoggerFactory.getLogger(VirtualMachine.class);

  public boolean isStarted() {
    return started;
  }

  /**
   * Only for initialization from the listener
   * 
   * @param jpf
   * @param executionThread
   */
  VirtualMachine(JPF jpf, Thread executionThread) {
    this(jpf);
    this.executionThread = executionThread;
  }

  public VirtualMachine(JPF jpf) {
    this.jpf = jpf;
  }

  public synchronized void startHook(VM vm, List<ClassInfo> postponedLoadedClasses) {
    logger.info("Notifying about vm started");
    VmStartEvent vmInitEvent = new VmStartEvent(vm.getCurrentThread());
    Jdwp.notify(vmInitEvent);

    logger.info("Sending postponed class loads.");
    for (ClassInfo classInfo : postponedLoadedClasses) {
      Event event = new ClassPrepareEvent(vm.getCurrentThread(), classInfo);
      Jdwp.notify(event);
    }
    postponedLoadedClasses.clear();
    logger.info("Sending postponed class loads... DONE");
  }

  public void notifyClassLoaded(ClassInfo lastClassInfo) {
    loadedClases.add(lastClassInfo);
  }

  public Collection<ClassInfo> getAllLoadedClasses() {
    return loadedClases;
  }

  private List<EventRequest<?>> requests = new CopyOnWriteArrayList<EventRequest<?>>();

  private ExecutionManager executionManager = new ExecutionManager();

  /**
   * The {@link ExecutionManager} class implements JPF specific behavior (given
   * by single threaded execution) and bridges the JDWP specification
   * requirements.<br/>
   * Note that this solution will never be perfect; just a trade-off.
   * 
   * @author stepan
   * 
   */
  public class ExecutionManager {
    boolean allThreadsSuspended = false;

    public boolean isAllThreadsSuspended() {
      return allThreadsSuspended;
    }

    /**
     * Should be called when JPF is not in the middle of instruction execution.<br/>
     * Should be called as often as possible so that JDWP commands can be run by
     * releasing the runlock.
     */
    public void executionHook() {
      suspendIfSuspended();
      exitIfInExit();

      // It is important to voluntarily let others to access and run JPF
      runLock.unlock();
      runLock.lock();
    }

    /**
     * Conditionally resumes execution of JPF.<br/>
     * JPF is resumed only and only if all the threads are marked as resumed.<br/>
     * As a consequence, JPF doesn't continue its execution unless user manually
     * resumes all the threads from the debugger.<br/>
     * 
     * Note that {@link ExecutionManager#markThreadResumed(ThreadInfo)} marks
     * given thread as resumed meanwhile
     * {@link ExecutionManager#markVMResumed()} marks all threads as resumed yet
     * it doesn't mean JPF will run since the number of resumptions of given
     * thread must be higher than its number of suspensions.
     */
    private synchronized void conditionallyResumeVM() {
      boolean resume = true;

      for (ThreadInfo liveThreadInfo : jpf.getVM().getLiveThreads()) {
        if (threadManager.suspensionCount(liveThreadInfo) > 0) {
          resume = false;
          logger.debug("Not resuming because of thread: {}", liveThreadInfo);
        }
      }

      if (resume) {
        resumeAllThreads();
      }
    }

    private synchronized void resumeAllThreads() {
      logger.debug("Resuming all threads by: {}", Thread.currentThread());
      allThreadsSuspended = false;
      this.notify();
    }

    private synchronized void suspendAllThreads() {
      allThreadsSuspended = true;
    }

    private ThreadManager threadManager = new ThreadManager();

    /**
     * Marks the threadInfo as suspended by the JDWP agent. This doesn't change
     * the {@link State} of the thread.<br/>
     * Doesn't suspend the thread immediately since we need to be able to finish
     * current instruction execution.<br/>
     * Since JPF is single threaded by design the given thread will be suspended
     * but no other thread will continue it's execution.
     * 
     * @param threadInfo
     *          The thread to be marked as suspended.
     */
    public synchronized void markThreadSuspended(ThreadInfo threadInfo) {
      threadManager.suspensionCountInc(threadInfo);
      suspendAllThreads();
    }

    /**
     * Marks the given <tt>threadInfo</tt> as resumed.<br/>
     * The JPF doesn't start it's execution immediately, but is notified and
     * will start the execution once all the locks can be acquired.
     * 
     * @param threadInfo
     */
    public synchronized void markThreadResumed(ThreadInfo threadInfo) {
      threadManager.suspensionCountDec(threadInfo);
      conditionallyResumeVM();
    }

    /**
     * Marks the whole VM as suspended.<br/>
     * This effectively works the same as single thread suspensions.
     * 
     * @see VirtualMachine#markThreadSuspended(ThreadInfo)
     */
    public synchronized void markVMSuspended() {
      suspendAllThreads();
      for (ThreadInfo threadInfo : jpf.getVM().getLiveThreads()) {
        threadManager.suspensionCountInc(threadInfo);
      }
    }

    /**
     * Marks the whole VM as resumed.<br/>
     * This effectively works the same as single thread resumption.
     * 
     * @see VirtualMachine#markThreadResumed(ThreadInfo)
     */
    public synchronized void markVMResumed() {
      for (ThreadInfo threadInfo : jpf.getVM().getLiveThreads()) {
        threadManager.suspensionCountDec(threadInfo);
      }
      conditionallyResumeVM();
    }

    /**
     * Suspend count for a thread.
     * 
     * @param threadInfo
     *          The thread.
     * @return The number of suspension count
     */
    public int suspendCount(ThreadInfo threadInfo) {
      return threadManager.suspensionCount(threadInfo);
    }

    /**
     * Whether the given thread is suspended by the debugger.
     * 
     * @param threadInfo
     *          The thread.
     * @return True or false.
     */
    public boolean isThreadSuspended(ThreadInfo threadInfo) {
      return suspendCount(threadInfo) > 0;
    }

    /**
     * Blocks the execution of JPF.<br/>
     * It is allowed to block only from the thread that runs JPF itself. If
     * violated, {@link IllegalStateException} is thrown.<br/>
     * Uses <tt>this</tt> object for waiting.
     */
    public synchronized void blockVMExecution() {
      accessThreadCheck();
      try {
        logger.debug("Suspending all threads in: {}", Thread.currentThread());
        runLock.unlock();
        this.wait();
      } catch (InterruptedException e) {
      } finally {
        logger.debug("All threads resumed in: {}", Thread.currentThread());
        runLock.lock();
        exitIfInExit();
      }
    }

    private synchronized void suspendIfSuspended() {
      while (allThreadsSuspended) {
        blockVMExecution();
      }
    }
  }

  public List<EventRequest<?>> getRequests() {
    return requests;
  }

  public void registerEventRequest(EventRequest<?> eventRequest) {
    requests.add(eventRequest);
  }

  public JPF getJpf() {
    return jpf;
  }

  public static class Capabilities {

    /**
     * Can the VM watch field modification, and therefore can it send the
     * Modification Watchpoint Event?
     */
    public static final boolean CAN_WATCH_FIELD_MODIFICATION = true;

    /**
     * Can the VM watch field access, and therefore can it send the Access
     * Watchpoint Event?
     */
    public static final boolean CAN_WATCH_FIELD_ACCESS = true;

    /** Can the VM get the bytecodes of a given method? */
    public static final boolean CAN_GET_BYTECODES = false;

    /**
     * Can the VM determine whether a field or method is synthetic? (that is,
     * can the VM determine if the method or the field was invented by the
     * compiler?)
     */
    public static final boolean CAN_GET_SYNTHETIC_ATTRIBUTE = true;

    /** Can the VM get the owned monitors information for a thread? */
    public static final boolean CAN_GET_OWNED_MONITOR_INFO = true;

    /** Can the VM get the current contended monitor of a thread? */
    public static final boolean CAN_GET_CURRENT_CONTENDED_MONITOR = true;

    /** Can the VM get the monitor information for a given object? */
    public static final boolean CAN_GET_MONITOR_INFO = true;
  }

  public static class CapabilitiesNew {

    /**
     * Can the VM watch field modification, and therefore can it send the
     * Modification Watchpoint Event?
     */
    public static final boolean CAN_WATCH_FIELD_MODIFICATION = Capabilities.CAN_WATCH_FIELD_MODIFICATION;

    /**
     * Can the VM watch field access, and therefore can it send the Access
     * Watchpoint Event?
     */
    public static final boolean CAN_WATCH_FIELD_ACCESS = Capabilities.CAN_WATCH_FIELD_ACCESS;

    /** Can the VM get the bytecodes of a given method? */
    public static final boolean CAN_GET_BYTECODES = Capabilities.CAN_GET_BYTECODES;

    /**
     * Can the VM determine whether a field or method is synthetic? (that is,
     * can the VM determine if the method or the field was invented by the
     * compiler?)
     */
    public static final boolean CAN_GET_SYNTHETIC_ATTRIBUTE = Capabilities.CAN_GET_SYNTHETIC_ATTRIBUTE;

    /** Can the VM get the owned monitors information for a thread? */
    public static final boolean CAN_GET_OWNED_MONITOR_INFO = Capabilities.CAN_GET_OWNED_MONITOR_INFO;

    /** Can the VM get the current contended monitor of a thread? */
    public static final boolean CAN_GET_CURRENT_CONTENDED_MONITOR = Capabilities.CAN_GET_CURRENT_CONTENDED_MONITOR;

    /** Can the VM get the monitor information for a given object? */
    public static final boolean CAN_GET_MONITOR_INFO = Capabilities.CAN_GET_MONITOR_INFO;

    /** Can the VM redefine classes? */
    public static final boolean CAN_REDEFINE_CLASSES = false;

    /** Can the VM add methods when redefining classes? */
    public static final boolean CAN_ADD_METHOD = false;

    /** Can the VM redefine classes in arbitrary ways? */
    public static final boolean CAN_UNRESTRICTEDLY_REDEFINE_CLASSES = false;

    /** Can the VM pop stack frames? */
    public static final boolean CAN_POP_FRAMES = true;

    /** Can the VM filter events by specific object? */
    public static final boolean CAN_USE_INSTANCE_FILTERS = true;

    /** Can the VM get the source debug extension? */
    public static final boolean CAN_GET_SOURCE_DEBUG_EXTENSION = false;

    /** Can the VM request VM death events? */
    public static final boolean CAN_REQUEST_V_M_DEATH_EVENT = true;

    /** Can the VM set a default stratum? */
    public static final boolean CAN_SET_DEFAULT_STRATUM = false;

    /**
     * Can the VM return instances, counts of instances of classes and referring
     * objects?
     */
    public static final boolean CAN_GET_INSTANCE_INFO = true;

    /** Can the VM request monitor events? */
    public static final boolean CAN_REQUEST_MONITOR_EVENTS = true;

    /** Can the VM get monitors with frame depth info? */
    public static final boolean CAN_GET_MONITOR_FRAME_INFO = false;

    /** Can the VM filter class prepare events by source name? */
    public static final boolean CAN_USE_SOURCE_NAME_FILTERS = false;

    /** Can the VM return the constant pool information? */
    public static final boolean CAN_GET_CONSTANT_POOL = false;

    /** Can the VM force early return from a method? */
    public static final boolean CAN_FORCE_EARLY_RETURN = false;
  }

  private List<ObjectId> disableCollectionObjects = new CopyOnWriteArrayList<ObjectId>();

  public void disableCollection(ObjectId objectId) throws InvalidObjectException {
    synchronized (disableCollectionObjects) {
      objectId.disableCollection();
      if (!disableCollectionObjects.contains(objectId)) {
        disableCollectionObjects.add(objectId);
      }
    }

  }

  public void enableCollection(ObjectId objectId) throws InvalidObjectException {
    synchronized (disableCollectionObjects) {
      objectId.enableCollection();
      if (disableCollectionObjects.contains(objectId)) {
        disableCollectionObjects.remove(objectId);
      }
    }
  }

  public void collectAllDisabledObjects() {
    synchronized (disableCollectionObjects) {
      for (Iterator<ObjectId> iteratorObjectId = disableCollectionObjects.iterator(); iteratorObjectId.hasNext();) {
        ObjectId objectId = iteratorObjectId.next();

        try {
          objectId.enableCollection();
        } catch (InvalidObjectException e) {
        }
        disableCollectionObjects.remove(objectId);
      }
    }
  }

  private int exitCode = 0;
  private boolean inExit = false;

  /**
   * Checks whether a proper thread is running the code.
   */
  private void accessThreadCheck() {
    if (executionThread != Thread.currentThread()) {
      throw new IllegalStateException("Access check violated from: " + Thread.currentThread());
    }
  }

  public class ThreadManager {

    private final Map<Integer, Integer> threadContextDataMap = new HashMap<Integer, Integer>();

    public synchronized void suspensionCountInc(ThreadInfo threadInfo) {
      int suspensionCount = 1;
      int threadIntId = threadInfo.getThreadObjectRef();
      
      if (threadContextDataMap.containsKey(threadIntId)) {
        suspensionCount = threadContextDataMap.get(threadIntId);
        threadContextDataMap.put(threadIntId, ++suspensionCount);
      } else {
        threadContextDataMap.put(threadIntId, suspensionCount);
      }
      
      logger.debug("Suspension count of thread {} increased to: {}", threadInfo, suspensionCount);
    }

    public synchronized void suspensionCountDec(ThreadInfo threadInfo) {
      int suspensionCount = 0;
      int threadIntId = threadInfo.getThreadObjectRef();
      
      if (threadContextDataMap.containsKey(threadIntId)) {
        suspensionCount = threadContextDataMap.get(threadIntId);
        if (suspensionCount > 0) {
          threadContextDataMap.put(threadIntId, --suspensionCount);
        }
      } else {
        // this can really happen - we don't have a thread and it is being suspended
        // if a thread is started it's not here yet, then some other thread is suspended
        // this new thread is not here yet and then all threads are resumed and bang
        // we're here
        threadContextDataMap.put(threadIntId, suspensionCount);
      }
      
      logger.debug("Suspension count of thread {} decreased to: {}", threadInfo, suspensionCount);
    }

    public synchronized int suspensionCount(ThreadInfo threadInfo) {
      int threadIntId = threadInfo.getThreadObjectRef();
      if (threadContextDataMap.containsKey(threadIntId)) {
        return threadContextDataMap.get(threadIntId);
      } else {
        return 0;
      }
    }

  }

  public int lastCreatedString = -1;

  private SafeLock runLock = new SafeLock("run-lock");
  private Thread executionThread;
  private Jdwp jdwp;

  public SafeLock getRunLock() {
    return runLock;
  }

  /**
   * Instructs JPF and JDWP threads to exit. This method is only a trigger of
   * all shutdown sequences.
   * 
   * @see VirtualMachine#exitIfInExit()
   * @see VirtualMachine#run()
   */
  public void exit(int exitCode) {
    if (!inExit) {
      this.exitCode = exitCode;
      this.inExit = true;

      // resume JPF so that it can exit
      this.executionManager.resumeAllThreads();
    }
  }

  /**
   * Conditionally exits execution on JPF.<br/>
   * Must be called by JPF thread.<br/>
   * Currently JPF exit is implemented with throw of {@link ExitException} which
   * is caught in {@link VirtualMachine#run()} from where the exit code from
   * {@link VirtualMachine#exit(int)} is used.
   */
  public void exitIfInExit() {
    if (inExit) {
      accessThreadCheck();

      // This won't print anything interesting ... but there is no better way to
      // exit JPF thread is there?
      JPF.exit();
    }
  }

  /**
   * Runs the virtual machine hence the JPF.
   * 
   * @return The exit code
   */
  public int run() {
    executionThread = Thread.currentThread();
    try {
      // Get the lock before the JPF starts.
      // This lock is paired with unlock at the end of the finally block
      // .. see a comment there for further detail
      runLock.lock();
      jpf.run();
      inExit = true;
    } catch (ExitException ee) {
      logger.warn("JPF was forcibly closed. Exiting...", ee);
    } catch (Throwable t) {
      logger.error("An uncaught exception in JPF thrown. Exiting...", t);
    } finally {
      try {
        Jdwp.notify(new VmDeathEvent());
      } catch (Throwable t) {
        logger.info("VM Death event NOT successfully sent.");
        // we're about to end anyway, thus do nothing
      }

      jdwp.shutdown();

      // the unlock here is just for packet processor to be able to finish
      // if the lock is owned
      // We cannot be sure whether the lock is owned or not since the
      // Exception could have been thrown from both locked and unlocked
      // sections
      runLock.unlockIfOwned();
    }
    return exitCode;
  }

  public ExecutionManager getExecutionManager() {
    return executionManager;
  }

  public void setJdwp(Jdwp jdwp) {
    this.jdwp = jdwp;
  }

  public void holdEvents() {
    jdwp.holdEvents();
  }

  public void releaseEvents() {
    jdwp.releaseEvents();
  }
}
