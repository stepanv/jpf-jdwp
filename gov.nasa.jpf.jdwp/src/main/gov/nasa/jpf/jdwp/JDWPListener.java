package gov.nasa.jpf.jdwp;

import gnu.classpath.jdwp.Jdwp;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jdwp.event.BreakpointEvent;
import gov.nasa.jpf.jdwp.event.ClassPrepareEvent;
import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.ExceptionEvent;
import gov.nasa.jpf.jdwp.event.FieldAccessEvent;
import gov.nasa.jpf.jdwp.event.FieldModificationEvent;
import gov.nasa.jpf.jdwp.event.MethodEntryEvent;
import gov.nasa.jpf.jdwp.event.SingleStepEvent;
import gov.nasa.jpf.jdwp.event.ThreadStartEvent;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.jvm.bytecode.FieldInstruction;
import gov.nasa.jpf.jvm.bytecode.GETFIELD;
import gov.nasa.jpf.jvm.bytecode.GETSTATIC;
import gov.nasa.jpf.jvm.bytecode.InstructionVisitorAdapter;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.PUTFIELD;
import gov.nasa.jpf.jvm.bytecode.PUTSTATIC;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ExceptionHandler;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.VMListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDWPListener extends ListenerAdapter implements VMListener {

	private class FieldVisitor extends InstructionVisitorAdapter {

		private ThreadInfo threadInfo;

		/**
		 * Field access handler for standard instances and also statics of any
		 * class.
		 * 
		 * @param fieldInstruction
		 *            Instruction that has to be investigated
		 * @param objectBeingAccessed
		 *            Object being accessed or null for statics
		 */
		private void fieldAccess(FieldInstruction fieldInstruction, ElementInfo objectBeingAccessed) {

			ClassInfo fieldClassInfo = fieldInstruction.getFieldInfo().getTypeClassInfo();
			Event event = new FieldAccessEvent(threadInfo, Location.factorySafe(fieldInstruction, threadInfo), fieldClassInfo, fieldInstruction.getFieldInfo(),
					objectBeingAccessed);
			dispatchEvent(event);
		}

		/**
		 * Field modification handler for standard instances and also statics of
		 * any class.
		 * 
		 * @param fieldInstruction
		 *            Instruction that has to be investigated
		 * @param objectBeingAccessed
		 *            Object being accessed or null for statics
		 */
		private void fieldModification(FieldInstruction fieldInstruction, ElementInfo objectBeingAccessed) {
			StackFrame topStackFrame = threadInfo.getModifiableTopFrame();

			ClassInfo fieldClassInfo = fieldInstruction.getFieldInfo().getTypeClassInfo();
			Tag tag = Tag.classInfoToTag(fieldClassInfo);

			Event event = new FieldModificationEvent(threadInfo, Location.factorySafe(fieldInstruction, threadInfo), fieldClassInfo,
					fieldInstruction.getFieldInfo(), objectBeingAccessed, tag, topStackFrame);
			dispatchEvent(event);
		}

		@Override
		public void visit(GETFIELD ins) {
			fieldAccess(ins, ins.getLastElementInfo());
		}

		@Override
		public void visit(PUTFIELD ins) {
			fieldModification(ins, ins.getLastElementInfo());
		}

		@Override
		public void visit(GETSTATIC ins) {
			fieldAccess(ins, null);
		}

		@Override
		public void visit(PUTSTATIC ins) {

			fieldModification(ins, null);
		}

		public void initalize(ThreadInfo currentThread) {
			this.threadInfo = currentThread;
		}

	}

	private VirtualMachine virtualMachine;
	private FieldVisitor fieldVisitor;

	public JDWPListener(JPF jpf, VirtualMachine virtualMachine) {
		this.virtualMachine = virtualMachine;
		this.fieldVisitor = new FieldVisitor();
	}

	@Override
	public void methodEntered(VM vm, ThreadInfo currentThread, MethodInfo enteredMethod) {
		virtualMachine.started(vm, postponedLoadedClasses);

		Instruction instruction = enteredMethod.getInstruction(0);
		if (instruction.getMethodInfo() != null && instruction.getMethodInfo().getClassInfo() != null) {
			MethodEntryEvent methodEntryEvent = new MethodEntryEvent(currentThread, Location.factory(instruction));
			dispatchEvent(methodEntryEvent);
		}
	}

	@Override
	public void threadStarted(VM vm, ThreadInfo startedThread) {
		logger.info("All threads: {}", Arrays.toString(VM.getVM().getLiveThreads()));

		for (ThreadInfo threadInfo : VM.getVM().getLiveThreads()) {
			logger.debug("threadInfo: {} ... dynamic object: {}", threadInfo, threadInfo.getThreadObject());
			logger.debug("ID by threadInfo: {}", JdwpObjectManager.getInstance().getThreadId(threadInfo));
			logger.debug("ID by thread object: {}", JdwpObjectManager.getInstance().getObjectId(threadInfo.getThreadObject()));
		}

		ThreadStartEvent threadStartEvent = new ThreadStartEvent(startedThread);

		logger.info("Started thread: " + startedThread);

		dispatchEvent(threadStartEvent);
	}

	@Override
	public void threadTerminated(VM vm, ThreadInfo terminatedThread) {
		// TODO [for PJA] there is not relation 1:1 between thread start and
		// thread death events. (e.g. one thread can die multiple times) and JDI
		// doesn't know what to do about that.
		// if (vmJdi.getEventRequestManager().threadDeathRequests().size() > 0)
		// {
		// ThreadDeathEvent td = new ThreadDeathEventImpl(vmJdi,
		// vm.getLastThreadInfo(),
		// vmJdi.getEventRequestManager().threadDeathRequests().get(0));
		// vmJdi.addEvent(td);
		// }
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
			ClassPrepareEvent classPrepareEvent = new ClassPrepareEvent(vm.getCurrentThread(), loadedClass, 0);
			dispatchEvent(classPrepareEvent);
		} else {
			logger.info("NOT NOTIFYING ABOUT: {}", loadedClass);
			postponedLoadedClasses.add(loadedClass);
		}
	}
	
	final static Logger logger = LoggerFactory.getLogger(JDWPListener.class);

	@Override
	public void executeInstruction(VM vm, ThreadInfo currentThread, Instruction instructionToExecute) {
		virtualMachine.suspendIfSuspended();
		virtualMachine.started(vm, postponedLoadedClasses);
		if (instructionToExecute.getMethodInfo() != null && instructionToExecute.getMethodInfo().getClassInfo() != null) {

			// TODO Breakpoint events and step events are supposed to be in one
			// composite event if occurred together!
			if (logger.isTraceEnabled()) {
				if (instructionToExecute instanceof InvokeInstruction) {
					logger.trace("Instruction: '{}' args: {} line: {}", instructionToExecute, ((InvokeInstruction) instructionToExecute).arguments, instructionToExecute.getFileLocation());
				} else {
					logger.trace("Instruction: '{}' line: {}", instructionToExecute, instructionToExecute.getFileLocation());
				}
			}
			Location locationOfInstructionToExecute = Location.factory(instructionToExecute);

			BreakpointEvent breakpointEvent = new BreakpointEvent(currentThread, locationOfInstructionToExecute);
			dispatchEvent(breakpointEvent);

			if (instructionToExecute instanceof FieldInstruction) {
				fieldVisitor.initalize(currentThread);
				((FieldInstruction) instructionToExecute).accept(fieldVisitor);
			}

			SingleStepEvent singleStepEvent = new SingleStepEvent(currentThread, locationOfInstructionToExecute);
			dispatchEvent(singleStepEvent);
		}
	}

	public void uncaughtExceptionThrown(VM vm, ThreadInfo currentThread, ElementInfo thrownException) {
		Instruction instruction = vm.getInstruction();
		if (instruction != null) {
			ExceptionEvent exceptionEvent = new ExceptionEvent(currentThread, Location.factorySafe(instruction, currentThread), thrownException, null);
			dispatchEvent(exceptionEvent);
		}
	}

	public void caughtExceptionThrown(VM vm, ThreadInfo currentThread, ElementInfo thrownException, StackFrame handlerFrame, ExceptionHandler matchingHandler) {
		Instruction instruction = vm.getInstruction();
		MethodInfo handlerMethodInfo = handlerFrame.getMethodInfo();
		int handlerInstructionIndex = matchingHandler.getHandler();

		Instruction catchInstruction = handlerMethodInfo.getInstructionAt(handlerInstructionIndex);

		if (instruction != null && catchInstruction != null) {
			ExceptionEvent exceptionEvent = new ExceptionEvent(currentThread, Location.factorySafe(instruction, currentThread), thrownException,
					Location.factorySafe(catchInstruction, currentThread));
			dispatchEvent(exceptionEvent);
		} else {
			// TODO what if we get an exception without possibility to get a
			// position?
			throw new RuntimeException("NOT IMPLEMENTED");
		}
	}

	@Override
	public void exceptionThrown(VM vm, ThreadInfo currentThread, ElementInfo thrownException) {
		logger.debug("Exception thrown: {}", thrownException);
		StackFrame handlerFrame = currentThread.getPendingExceptionHandlerFrame();
		ExceptionHandler exceptionHandler = currentThread.getPendingExceptionMatchingHandler();

		if (handlerFrame != null && exceptionHandler != null) {
			caughtExceptionThrown(vm, currentThread, thrownException, handlerFrame, exceptionHandler);
		} else {
			uncaughtExceptionThrown(vm, currentThread, thrownException);
		}
	}

	private void dispatchEvent(Event event) {
		synchronized (virtualMachine) {
			Jdwp.notify(event);
		}

	}

}
