package test.jdi.impl;

import test.jdi.impl.internal.JPFManager;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.VMListener;

public class JDIListener extends ListenerAdapter implements VMListener {

	VirtualMachineImpl vmJdi;
	JPFManager jpfManager;
	
	public JDIListener(VirtualMachineImpl vmJdi) {
		this.vmJdi = vmJdi;
	}
	
	@Override
	public void methodEntered (JVM vm) {
		vmJdi.started();
	}
	
	@Override
	public void executeInstruction(JVM vm) {
		jpfManager.handlePossibleBreakpointHit();
	}
}
