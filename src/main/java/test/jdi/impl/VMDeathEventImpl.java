package test.jdi.impl;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.VMDeathRequest;

public class VMDeathEventImpl implements VMDeathEvent {

	private VirtualMachineImpl vm;
	private VMDeathRequest request;

	public VMDeathEventImpl(VirtualMachineImpl virtualMachineImpl,
			VMDeathRequest vmDeathRequest) {
		this.vm = virtualMachineImpl;
		this.request = vmDeathRequest;
	}

	@Override
	public EventRequest request() {
		return request;
	}

	@Override
	public VirtualMachine virtualMachine() {
		return vm;
	}

}
