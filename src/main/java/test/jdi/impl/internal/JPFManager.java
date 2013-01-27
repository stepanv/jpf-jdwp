package test.jdi.impl.internal;

import test.jdi.impl.VirtualMachineImpl;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;

public class JPFManager {

	private VirtualMachineImpl vm;

	public JPFManager(VirtualMachineImpl virtualMachineImpl) {
		this.vm = virtualMachineImpl;
	}

	public void handlePossibleBreakpointHit() {
		
		
		
		JVM jvm = vm.getJvm();
	      ThreadInfo ti = jvm.getCurrentThread();
	        Instruction instruction = jvm.getLastInstruction();
	        
	        if (vm.getEventRequestManager().getBreakpointManager().isBreakpointHit(instruction)) {
	        	ti.suspend();
	        }
		// TODO Auto-generated method stub
		
	}

}
