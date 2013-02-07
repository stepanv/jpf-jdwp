package test.jdi.impl.event;

import test.jdi.impl.VirtualMachineImpl;
import test.jdi.impl.request.VMDeathRequestImpl;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.VMDeathRequest;

public class VMDeathEventImpl extends EventImpl implements VMDeathEvent {


	public VMDeathEventImpl(VirtualMachineImpl virtualMachineImpl,
			VMDeathRequestImpl vmDeathRequest) {
		super(virtualMachineImpl, vmDeathRequest);
	}


}
