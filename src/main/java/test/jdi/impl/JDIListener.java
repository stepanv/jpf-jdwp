package test.jdi.impl;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.VMListener;

public class JDIListener extends ListenerAdapter implements VMListener {

	VirtualMachineImpl vmJdi;
	
	public JDIListener(VirtualMachineImpl vmJdi) {
		this.vmJdi = vmJdi;
	}
	
	public void methodEntered (JVM vm) {
		vmJdi.started();
	}
}
