package test.jdi.impl;

import org.apache.log4j.Logger;

import gov.nasa.jpf.inspector.interfaces.BreakPointStatus;
import gov.nasa.jpf.jvm.ThreadInfo;

import com.sun.jdi.Location;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.request.EventRequest;

public class BreakpointEventImpl implements BreakpointEvent {

	private BreakPointStatus bp;
	private VirtualMachineImpl vm;
	private BreakpointRequestImpl bRequest;
	private ThreadInfo ti;
	
	public static final Logger log = org.apache.log4j.Logger.getLogger(BreakpointEventImpl.class);

	public BreakpointEventImpl(BreakpointRequestImpl bRequest, VirtualMachineImpl vm, ThreadInfo ti) {
		this.vm = vm;
		this.bRequest = bRequest;
		this.ti = ti;
	}

	@Override
	public VirtualMachine virtualMachine() {
		log.debug("method entering");
		return vm;
	}

	@Override
	public EventRequest request() {
		log.debug("method entering");
		return bRequest;
	}

	@Override
	public ThreadReference thread() {
		log.debug("method entering");
		return vm.getThreads().get(ti);
	}

	@Override
	public Location location() {
		return bRequest.location();
	}

}
