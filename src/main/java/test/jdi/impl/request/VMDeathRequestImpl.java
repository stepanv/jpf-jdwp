package test.jdi.impl.request;

import test.jdi.impl.VirtualMachineImpl;

import com.sun.jdi.request.VMDeathRequest;

public class VMDeathRequestImpl extends EventRequestImpl implements VMDeathRequest {

	public VMDeathRequestImpl(VirtualMachineImpl vm) {
		super(vm);
	}
}
