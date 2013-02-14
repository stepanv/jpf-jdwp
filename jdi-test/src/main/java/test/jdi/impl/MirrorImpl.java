package test.jdi.impl;

import com.sun.jdi.Mirror;
import com.sun.jdi.VirtualMachine;

public class MirrorImpl implements Mirror {

	protected VirtualMachineImpl vm;
	
	public MirrorImpl(VirtualMachineImpl vm) {
		this.vm = vm;
	}

	@Override
	public VirtualMachine virtualMachine() {
		return vm;
	}

}
