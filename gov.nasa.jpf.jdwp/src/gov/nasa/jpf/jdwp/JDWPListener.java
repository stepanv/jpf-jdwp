package gov.nasa.jpf.jdwp;


import java.util.ArrayList;
import java.util.List;

import gnu.classpath.jdwp.Jdwp;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jdwp.event.BreakpointEvent;
import gov.nasa.jpf.jdwp.event.ClassPrepareEvent;
import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.MethodEntryEvent;
import gov.nasa.jpf.jdwp.event.SingleStepEvent;
import gov.nasa.jpf.jdwp.event.ThreadStartEvent;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.VMListener;
import gov.nasa.jpf.jvm.bytecode.Instruction;

public class JDWPListener extends ListenerAdapter implements VMListener {

	private VirtualMachine virtualMachine;
	
	public JDWPListener(JPF jpf, VirtualMachine virtualMachine) {
		this.virtualMachine = virtualMachine;
	}
	
	@Override
	public void methodEntered (JVM vm) {
		virtualMachine.started(vm, postponedLoadedClasses);
		
		Instruction instruction = vm.getLastMethodInfo().getInstruction(0);
		if (instruction.getMethodInfo() != null && instruction.getMethodInfo().getClassInfo() != null) {
			ThreadId threadId = (ThreadId) JdwpObjectManager.getInstance().getObjectId(vm.getLastThreadInfo());
			MethodEntryEvent methodEntryEvent = new MethodEntryEvent(threadId, Location.factory(instruction));
			dispatchEvent(methodEntryEvent);
		}
	}
	
	

	@Override
	public void threadStarted(JVM vm) {
		ThreadStartEvent threadStartEvent = new ThreadStartEvent(vm.getLastThreadInfo());
		
		dispatchEvent(threadStartEvent);
	}
	
	@Override
	public void threadTerminated(JVM vm) {
		// TODO [for PJA] there is not relation 1:1 between thread start and thread death events. (e.g. one thread can die multiple times) and JDI doesn't know what to do about that.
		//if (vmJdi.getEventRequestManager().threadDeathRequests().size() > 0) {
			//ThreadDeathEvent td = new ThreadDeathEventImpl(vmJdi, vm.getLastThreadInfo(), vmJdi.getEventRequestManager().threadDeathRequests().get(0));
			//vmJdi.addEvent(td);
		//}
	}
	
	List<ClassInfo> postponedLoadedClasses = new ArrayList<ClassInfo>();
	private Instruction lastInstruction;
	@Override
	public void classLoaded(JVM vm) {
		virtualMachine.notifyClassLoaded(vm.getLastClassInfo());
		// TODO [for PJA] This is weird.. According to JDWP we should sent threadID where this class loaded event occurred
		// but in case of JPF it doesn't have a system thread 
		// (which caused class load before the main thread was executed) .. does it?
		if (vm.getCurrentThread() != null) {
			ClassPrepareEvent classPrepareEvent = new ClassPrepareEvent(vm.getCurrentThread(), vm.getLastClassInfo(), 0);
			dispatchEvent(classPrepareEvent);
		} else {
			System.out.println("NOT NOTIFYING ABOUT: " + vm.getLastClassInfo());
			postponedLoadedClasses.add(vm.getLastClassInfo());
		}
	}
	
	@Override
	public void executeInstruction(JVM vm) {
		virtualMachine.started(vm, postponedLoadedClasses);
		Instruction nextInstruction = vm.getNextInstruction();
		if (nextInstruction.getMethodInfo() != null && nextInstruction.getMethodInfo().getClassInfo() != null) {
			ThreadId threadId = (ThreadId) JdwpObjectManager.getInstance().getObjectId(vm.getCurrentThread());
			BreakpointEvent breakpointEvent = new BreakpointEvent(threadId, Location.factory(nextInstruction));
			dispatchEvent(breakpointEvent);
			
			// TODO Breakpoint events and step events are supposed to be in one composite event if occurred together!
			
			//virtualMachine.conditionallyTriggerStepEvent(vm);
			SingleStepEvent singleStepEvent = new SingleStepEvent(threadId, Location.factory(nextInstruction));
			dispatchEvent(singleStepEvent);
		}
		lastInstruction = nextInstruction;
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
