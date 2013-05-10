package gov.nasa.jpf.jdwp;


import java.util.ArrayList;
import java.util.List;

import gnu.classpath.jdwp.Jdwp;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jdwp.event.BreakpointEvent;
import gov.nasa.jpf.jdwp.event.ClassPrepareEvent;
import gov.nasa.jpf.jdwp.event.EventBase;
import gov.nasa.jpf.jdwp.event.MethodEntryEvent;
import gov.nasa.jpf.jdwp.event.SingleStepEvent;
import gov.nasa.jpf.jdwp.event.ThreadStartEvent;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.VMListener;

public class JDWPListener extends ListenerAdapter implements VMListener {

	private VirtualMachine virtualMachine;
	
	public JDWPListener(JPF jpf, VirtualMachine virtualMachine) {
		this.virtualMachine = virtualMachine;
	}
	
	@Override
	public void methodEntered (VM vm, ThreadInfo currentThread, MethodInfo enteredMethod) {
		virtualMachine.started(vm, postponedLoadedClasses);
		
		Instruction instruction = enteredMethod.getInstruction(0);
		if (instruction.getMethodInfo() != null && instruction.getMethodInfo().getClassInfo() != null) {
			ThreadId threadId = (ThreadId) JdwpObjectManager.getInstance().getObjectId(currentThread);
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
			ThreadId threadId = (ThreadId) JdwpObjectManager.getInstance().getObjectId(vm.getCurrentThread());
			BreakpointEvent breakpointEvent = new BreakpointEvent(threadId, Location.factory(instructionToExecute));
			dispatchEvent(breakpointEvent);
			
			// TODO Breakpoint events and step events are supposed to be in one composite event if occurred together!
			if (instructionToExecute instanceof InvokeInstruction) {
				//System.out.println("Instruction: '" + instructionToExecute + "' args: " + ((InvokeInstruction)instructionToExecute).arguments +" line: " + instructionToExecute.getLineNumber());
			} else {
				//System.out.println("Instruction: '" + instructionToExecute + "' line: " + instructionToExecute.getLineNumber());	
			}
			
			//virtualMachine.conditionallyTriggerStepEvent(vm);
			SingleStepEvent singleStepEvent = new SingleStepEvent(threadId, Location.factory(instructionToExecute));
			dispatchEvent(singleStepEvent);
		}
		lastInstruction = instructionToExecute;
	}
	
	private void dispatchEvent(EventBase event) {
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
