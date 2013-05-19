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
import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
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
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.VMListener;

import java.util.ArrayList;
import java.util.List;

public class JDWPListener extends ListenerAdapter implements VMListener {
	
	private class FieldVisitor extends InstructionVisitorAdapter {


		private ThreadId threadId;
		private JdwpObjectManager objectManager;

		private void fieldAccess(FieldInstruction fieldInstruction) {
			ObjectId objectOrNull = objectManager.getObjectId(fieldInstruction.getLastElementInfo());
			
			ClassInfo fieldClassInfo = fieldInstruction.getFieldInfo().getTypeClassInfo();
			Event event;
			try {
				event = new FieldAccessEvent(threadId, Location.factorySafe(fieldInstruction, threadId.getInfoObject()), fieldClassInfo, objectManager.getFieldId(fieldInstruction.getFieldInfo()), objectOrNull);
				dispatchEvent(event);
			} catch (InvalidObject e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
		
		private void fieldModification(FieldInstruction fieldInstruction) {
			ThreadInfo threadInfo;
			try {
				threadInfo = threadId.getInfoObject();
			} catch (InvalidObject e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			StackFrame topStackFrame = threadInfo.getModifiableTopFrame();
			
			ClassInfo fieldClassInfo = fieldInstruction.getFieldInfo().getTypeClassInfo();
			Tag tag = Tag.classInfoToTag(fieldClassInfo);
			Value valueToBe = tag.peekValue(topStackFrame);
			ObjectId objectOrNull = objectManager.getObjectId(fieldInstruction.getLastElementInfo());
//			
//			
			Event event;
			try {
				event = new FieldModificationEvent(threadId, Location.factorySafe(fieldInstruction, threadId.getInfoObject()), fieldClassInfo, objectManager.getFieldId(fieldInstruction.getFieldInfo()), objectOrNull, valueToBe);
				dispatchEvent(event);
			} catch (InvalidObject e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void visit(GETFIELD ins) {
			fieldAccess(ins);
		}

		@Override
		public void visit(PUTFIELD ins) {
			fieldModification(ins);
		}

		@Override
		public void visit(GETSTATIC ins) {
			fieldAccess(ins);
		}

		@Override
		public void visit(PUTSTATIC ins) {
			fieldModification(ins);
		}

		public void initalize(ThreadInfo currentThread) {
			this.objectManager = JdwpObjectManager.getInstance();
			this.threadId = objectManager.getThreadId(currentThread);
		}
		
	}

	private VirtualMachine virtualMachine;
	private FieldVisitor fieldVisitor;
	
	public JDWPListener(JPF jpf, VirtualMachine virtualMachine) {
		this.virtualMachine = virtualMachine;
		this.fieldVisitor = new FieldVisitor();
	}
	
	@Override
	public void methodEntered (VM vm, ThreadInfo currentThread, MethodInfo enteredMethod) {
		virtualMachine.started(vm, postponedLoadedClasses);
		
		Instruction instruction = enteredMethod.getInstruction(0);
		if (instruction.getMethodInfo() != null && instruction.getMethodInfo().getClassInfo() != null) {
			ThreadId threadId = (ThreadId) JdwpObjectManager.getInstance().getThreadId(currentThread);
			MethodEntryEvent methodEntryEvent = new MethodEntryEvent(threadId, Location.factory(instruction));
			dispatchEvent(methodEntryEvent);
		}
	}

	@Override
	public void threadStarted(VM vm, ThreadInfo startedThread) {
		ThreadStartEvent threadStartEvent = new ThreadStartEvent(startedThread);
		
		dispatchEvent(threadStartEvent);
	}

	@Override
	public void threadTerminated(VM vm, ThreadInfo terminatedThread) {
		// TODO [for PJA] there is not relation 1:1 between thread start and thread death events. (e.g. one thread can die multiple times) and JDI doesn't know what to do about that.
		//if (vmJdi.getEventRequestManager().threadDeathRequests().size() > 0) {
			//ThreadDeathEvent td = new ThreadDeathEventImpl(vmJdi, vm.getLastThreadInfo(), vmJdi.getEventRequestManager().threadDeathRequests().get(0));
			//vmJdi.addEvent(td);
		//}
	}
	
	List<ClassInfo> postponedLoadedClasses = new ArrayList<ClassInfo>();
	private Instruction lastInstruction;
	@Override
	public void classLoaded(VM vm, ClassInfo loadedClass) {
		virtualMachine.notifyClassLoaded(loadedClass);
		// TODO [for PJA] This is weird.. According to JDWP we should sent threadID where this class loaded event occurred
		// but in case of JPF it doesn't have a system thread 
		// (which caused class load before the main thread was executed) .. does it?
		if (vm.getCurrentThread() != null && vm.isInitialized()) {
			ClassPrepareEvent classPrepareEvent = new ClassPrepareEvent(vm.getCurrentThread(), loadedClass, 0);
			dispatchEvent(classPrepareEvent);
		} else {
			System.out.println("NOT NOTIFYING ABOUT: " + loadedClass);
			postponedLoadedClasses.add(loadedClass);
		}
	}
	
	@Override
	public void executeInstruction(VM vm, ThreadInfo currentThread, Instruction instructionToExecute) {
		virtualMachine.started(vm, postponedLoadedClasses);
		if (instructionToExecute.getMethodInfo() != null && instructionToExecute.getMethodInfo().getClassInfo() != null) {
			ThreadId threadId = JdwpObjectManager.getInstance().getThreadId(vm.getCurrentThread());
			BreakpointEvent breakpointEvent = new BreakpointEvent(threadId, Location.factory(instructionToExecute));
			dispatchEvent(breakpointEvent);
			
			if (instructionToExecute instanceof FieldInstruction) {
				fieldVisitor.initalize(currentThread);
				((FieldInstruction)instructionToExecute).accept(fieldVisitor);
			}
			
			// TODO Breakpoint events and step events are supposed to be in one composite event if occurred together!
			if (instructionToExecute instanceof InvokeInstruction) {
				System.out.println("Instruction: '" + instructionToExecute + "' args: " + ((InvokeInstruction)instructionToExecute).arguments +" line: " + instructionToExecute.getLineNumber());
			} else {
				System.out.println("Instruction: '" + instructionToExecute + "' line: " + instructionToExecute.getLineNumber());	
			}
			
			ClassInfo classInfo = instructionToExecute.getMethodInfo().getClassInfo();
			for (FieldInfo fieldInfo : classInfo.getInstanceFields()) {
				int fieldIndex = fieldInfo.getFieldIndex();
				//classInfo.get
			}
			
			//new FieldAccessEvent(threadId, location, fieldType, fieldId, objectOrNull)
			
			//new FieldModificationEvent(threadId, location, fieldType, fieldId, objectOrNull, valueToBe)
			
			//virtualMachine.conditionallyTriggerStepEvent(vm);
			SingleStepEvent singleStepEvent = new SingleStepEvent(threadId, Location.factory(instructionToExecute));
			dispatchEvent(singleStepEvent);
		}
		lastInstruction = instructionToExecute;
	}
	
	
	@Override
	public void uncaughtExceptionThrown(VM vm, ThreadInfo currentThread, ElementInfo thrownException) {
		ThreadId threadId = JdwpObjectManager.getInstance().getThreadId(vm.getCurrentThread());
		Instruction instruction = vm.getInstruction();
		if (instruction != null) {
			ExceptionEvent exceptionEvent = new ExceptionEvent(threadId, Location.factorySafe(instruction, currentThread), thrownException, null);
			dispatchEvent(exceptionEvent);
		}
	}

	
	@Override
	public void caughtExceptionThrown(VM vm, ThreadInfo currentThread, ElementInfo thrownException, StackFrame handlerFrame, ExceptionHandler matchingHandler) {
		ThreadId threadId = JdwpObjectManager.getInstance().getThreadId(vm.getCurrentThread());
		Instruction instruction = vm.getInstruction();
		MethodInfo handlerMethodInfo = handlerFrame.getMethodInfo();
		int handlerInstructionIndex = matchingHandler.getHandler();
		
		Instruction catchInstruction = handlerMethodInfo.getInstructionAt(handlerInstructionIndex);
		
		if (instruction != null && catchInstruction != null) {
			ExceptionEvent exceptionEvent = new ExceptionEvent(threadId, Location.factorySafe(instruction, currentThread), thrownException, Location.factorySafe(catchInstruction, currentThread));
			dispatchEvent(exceptionEvent);
		} else {
			// TODO what if we get an exception without possibility to get a position?
		}
	}

	private void dispatchEvent(Event event) {
//		for (EventRequest request : requests) {
//			if (request.matches(event)) {
//				try {
//					
//					Jdwp.sendEvent(request, event);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
		Jdwp.notify(event);
	}
	
}
