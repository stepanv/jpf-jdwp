package test.jdi.impl;

import gov.nasa.jpf.inspector.interfaces.BreakPointStatus;

import com.sun.jdi.Location;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.request.EventRequest;

public class BreakpointEventImpl implements BreakpointEvent {

	private BreakPointStatus bp;
	private VirtualMachineImpl vmImpl;

	public BreakpointEventImpl(BreakPointStatus bp, VirtualMachineImpl vmImpl) {
		this.bp = bp;
		this.vmImpl = vmImpl;
	}

	@Override
	public VirtualMachine virtualMachine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventRequest request() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ThreadReference thread() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Location location() {
		// TODO Auto-generated method stub
		return null;
	}

}
