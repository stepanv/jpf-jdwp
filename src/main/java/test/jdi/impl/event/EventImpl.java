package test.jdi.impl.event;

import test.jdi.impl.VirtualMachineImpl;
import test.jdi.impl.request.EventRequestImpl;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.request.EventRequest;

public class EventImpl implements Event {
	
	protected VirtualMachineImpl vm;
	protected EventRequestImpl request;

	public EventImpl(VirtualMachineImpl vm, EventRequestImpl request) {
		this.vm = vm;
		this.request = request;
	}

	@Override
	public VirtualMachine virtualMachine() {
		return vm;
	}

	@Override
	public EventRequest request() {
		return request;
	}

}
