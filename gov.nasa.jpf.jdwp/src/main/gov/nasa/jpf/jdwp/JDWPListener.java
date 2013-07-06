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
import gov.nasa.jpf.jdwp.event.SingleStepEvent;
import gov.nasa.jpf.jdwp.event.ThreadDeathEvent;
import gov.nasa.jpf.jdwp.event.ThreadStartEvent;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.jdwp.util.FieldVisitor;
import gov.nasa.jpf.jvm.bytecode.FieldInstruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ExceptionHandler;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadInfo.State;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.VMListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDWPListener extends JDWPSearchBase implements VMListener {

	@Override
	public void objectReleased(VM vm, ThreadInfo currentThread, ElementInfo releasedObject) {
		logger.debug("Object released: {} .. ID: {}", releasedObject, releasedObject.getObjectRef());
	}

	private VirtualMachine virtualMachine;
	private FieldVisitor fieldVisitor = new FieldVisitor();

	public JDWPListener() {
		this.virtualMachine = new VirtualMachine(VM.getVM().getJPF(), Thread.currentThread());
		
		Jdwp jdwp = new Jdwp(virtualMachine);
		
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
		// This lock is paired with unlock at the end of the finally block
		// .. see a comment there for further detail
		virtualMachine.getRunLock().lock();
	}
	
	public JDWPListener(JPF jpf, VirtualMachine virtualMachine) {
		this.virtualMachine = virtualMachine;
	}

	@Override
	public void methodEntered(VM vm, ThreadInfo currentThread, MethodInfo enteredMethod) {
		Instruction instruction = enteredMethod.getInstruction(0);
		if (instruction.getMethodInfo() != null && instruction.getMethodInfo().getClassInfo() != null) {
			MethodEntryEvent methodEntryEvent = new MethodEntryEvent(currentThread, Location.factory(instruction));
			dispatchEvent(methodEntryEvent);
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
		// TODO [for PJA] I hate to say it but honestly startedThread should be
		// alread in state State.RUNNING by now - which is not by design ...
		// WHY???
		lastKnownThreadStates.put(startedThread.getThreadObjectRef(), State.RUNNING);
		logger.info("Started thread: " + startedThread);

		ThreadStartEvent threadStartEvent = new ThreadStartEvent(startedThread);
		dispatchEvent(threadStartEvent);
	}

	/**
	 * Thread termination handler.<br/>
	 * Note that there is not 1:1 relation between thread terminates and starts
	 * if states are traversed by JPF. Because of that a custom behavior needs
	 * to be implemented if not debugging a single trace.<br/>
	 * <h2>Eclipse debugger</h2>
	 * <ul>
	 * <li>Thread starts are ok - doesn't matter how many times a thread start
	 * is received (unless a race occurs which happens with Eclipse Juno for
	 * instance). In that case a thread can be shown multiple times in the Debug
	 * view.</li>
	 * <li>Thread deaths are so not ok. Eclipse implements deferred thread
	 * deaths handling if a thread is not known which makes thread disappear
	 * right after it is created and thread start event is received. TODO needs
	 * to be investigated more ... seems to be very tricky.<br/>
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
		// this is the workaround for Eclipse and it's deferred thread deaths
		// handling
		ThreadStartEvent ts = new ThreadStartEvent(terminatedThread);
		dispatchEvent(ts);

		lastKnownThreadStates.put(terminatedThread.getThreadObjectRef(), State.TERMINATED);
		logger.debug("Thread terminated: {}", terminatedThread);

		ThreadDeathEvent td = new ThreadDeathEvent(terminatedThread);
		dispatchEvent(td);
	}

	List<ClassInfo> postponedLoadedClasses = new ArrayList<ClassInfo>();

	@Override
	public void classLoaded(VM vm, ClassInfo loadedClass) {
		virtualMachine.notifyClassLoaded(loadedClass);
		// TODO [for PJA] This is weird.. According to JDWP we should sent
		// threadID where this class loaded event occurred
		// but in case of JPF it doesn't have a system thread
		// (which caused class load before the main thread was executed) .. does
		// it?
		if (vm.getCurrentThread() != null && vm.isInitialized()) {
			logger.debug("Class loaded: {}", loadedClass);
			ClassPrepareEvent classPrepareEvent = new ClassPrepareEvent(vm.getCurrentThread(), loadedClass, 0);
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

		// we also need to send thread start event
		// TODO [for PJA] is this a bug in JPF main thread start doesn't
		// trigger threadStarted event in JPF listeners
		ThreadStartEvent threadStartEvent = new ThreadStartEvent(vm.getCurrentThread());
		lastKnownThreadStates.put(vm.getCurrentThread().getThreadObjectRef(), State.RUNNING);
		dispatchEvent(threadStartEvent);
	}

	@Override
	public void executeInstruction(VM vm, ThreadInfo currentThread, Instruction instructionToExecute) {

		virtualMachine.getExecutionManager().executionHook();

		if (instructionToExecute.getMethodInfo() != null && instructionToExecute.getMethodInfo().getClassInfo() != null) {

			// TODO Breakpoint events and step events are supposed to be in one
			// composite event if occurred together!
			if (logger.isTraceEnabled()) {
				if (instructionToExecute instanceof InvokeInstruction) {
					// FIXME .. this requires InvokeInstruction#arguments to be public .. since this is just for debugging it has to be removed TODO
					logger.trace("Instruction: '{}' args: {} line: {}", instructionToExecute, ((InvokeInstruction) instructionToExecute).arguments,
							instructionToExecute.getFileLocation());
				} else {
					logger.trace("Instruction: '{}' line: {}", instructionToExecute, instructionToExecute.getFileLocation());
				}
			}
			Location locationOfInstructionToExecute = Location.factory(instructionToExecute);

			if (hasNonnullEventRequests(EventKind.BREAKPOINT)) {
				BreakpointEvent breakpointEvent = new BreakpointEvent(currentThread, locationOfInstructionToExecute);
				dispatchEvent(breakpointEvent);
			}

			if (hasNonnullEventRequests(EventKind.FIELD_ACCESS, EventKind.FIELD_MODIFICATION) && instructionToExecute instanceof FieldInstruction) {
				fieldVisitor.initalize(currentThread);
				((FieldInstruction) instructionToExecute).accept(fieldVisitor);
			}

			if (hasNonnullEventRequests(EventKind.SINGLE_STEP)) {
				SingleStepEvent singleStepEvent = new SingleStepEvent(currentThread, locationOfInstructionToExecute);
				dispatchEvent(singleStepEvent);
			}
		}
	}

	/**
	 * Whether the {@link EventRequestManager} has registered more than 0 event
	 * requests for the given event kind.
	 * 
	 * @param eventKind
	 *            The event kind.
	 * @return true or false
	 */
	private boolean hasNonnullEventRequests(EventKind eventKind) {
		return Jdwp.getEventRequestManager().eventRequestCount(eventKind) > 0;
	}

	/**
	 * Whether the {@link EventRequestManager} has registered more than 0 event request
	 * for at least one of the given event kinds.
	 * 
	 * @param firstEventKind
	 *            The first event kind.
	 * @param secondEventKind
	 *            The second event kind.
	 * @return true or false
	 */
	private boolean hasNonnullEventRequests(EventKind firstEventKind, EventKind secondEventKind) {
		return hasNonnullEventRequests(firstEventKind) || hasNonnullEventRequests(secondEventKind);
	}

	private void uncaughtExceptionThrown(VM vm, ThreadInfo currentThread, ElementInfo thrownException) {
		Instruction instruction = vm.getInstruction();
		if (instruction != null) {
			ExceptionEvent exceptionEvent = new ExceptionEvent(currentThread, Location.factorySafe(instruction, currentThread), thrownException, null);
			dispatchEvent(exceptionEvent);
		}
	}

	private void caughtExceptionThrown(VM vm, ThreadInfo currentThread, ElementInfo thrownException, StackFrame handlerFrame, ExceptionHandler matchingHandler) {
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
		StackFrame handlerFrame = currentThread.getPendingExceptionHandlerFrame();
		ExceptionHandler exceptionHandler = currentThread.getPendingExceptionMatchingHandler();

		if (handlerFrame != null && exceptionHandler != null) {
			caughtExceptionThrown(vm, currentThread, thrownException, handlerFrame, exceptionHandler);
		} else {
			uncaughtExceptionThrown(vm, currentThread, thrownException);
		}
	}

	/**
	 * A helper method for dispatching events.<br/>
	 * Note that this is not the only point where events are dispatched.
	 * 
	 * @param event
	 *            The event.
	 */
	private void dispatchEvent(Event event) {
		Jdwp.notify(event);
	}

	Map<Integer, State> lastKnownThreadStates = new HashMap<Integer, ThreadInfo.State>();

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
		logger.trace("Processing search");
		fixThreadNotificationState();
	}

	@Override
	public void stateAdvanced(Search search) {
		logger.trace("Processing search");
		fixThreadNotificationState();
	}

	@Override
	public void stateProcessed(Search search) {
		logger.trace("Processing search");
		fixThreadNotificationState();
	}

	@Override
	public void statePurged(Search search) {
		logger.trace("Processing search");
		fixThreadNotificationState();
	}

	@Override
	public void stateStored(Search search) {
		logger.trace("Processing search");
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
		logger.trace("Processing search");
		fixThreadNotificationState();
	}

}
