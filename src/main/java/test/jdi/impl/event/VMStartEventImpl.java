package test.jdi.impl.event;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.request.EventRequest;

public class VMStartEventImpl implements VMStartEvent {

	@Override
	public EventRequest request() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VirtualMachine virtualMachine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ThreadReference thread() {
		// TODO Auto-generated method stub
		return null;
	}

}
