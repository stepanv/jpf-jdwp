package test.jdi.impl.request;

import test.jdi.impl.EventRequestManagerImpl.EventRequestContainer;
import test.jdi.impl.VirtualMachineImpl;

import com.sun.jdi.request.VMDeathRequest;

public class VMDeathRequestImpl extends EventRequestImpl implements VMDeathRequest {

	public VMDeathRequestImpl(VirtualMachineImpl vm, EventRequestContainer<VMDeathRequest> vmDeathRequestContainer) {
		super(vm, vmDeathRequestContainer);
	}
}
