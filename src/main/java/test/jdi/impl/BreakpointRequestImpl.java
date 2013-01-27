package test.jdi.impl;

import org.apache.log4j.Logger;

import test.jdi.impl.internal.Breakpoint;

import com.sun.jdi.Location;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.BreakpointRequest;

public class BreakpointRequestImpl implements BreakpointRequest {

	public static final Logger log = org.apache.log4j.Logger.getLogger(BreakpointRequestImpl.class);
	VirtualMachine vm;
	private Location location;
	private Breakpoint breakpoint;
	
	public BreakpointRequestImpl(VirtualMachine vm, Location location) {
		this.vm = vm;
		this.location = location;
	}
	
	@Override
	public void addCountFilter(int count) {
		log.debug("method enter");

	}

	@Override
	public void disable() {
		log.debug("method enter");

	}

	@Override
	public void enable() {
		log.debug("method enter");

	}

	@Override
	public Object getProperty(Object key) {
		log.debug("method enter");
		return null;
	}

	@Override
	public boolean isEnabled() {
		log.debug("method enter");
		return false;
	}

	@Override
	public void putProperty(Object key, Object value) {
		log.debug("method enter");

	}

	@Override
	public void setEnabled(boolean val) {
		log.debug("method enter");

	}

	@Override
	public void setSuspendPolicy(int policy) {
		log.debug("method enter");

	}

	@Override
	public int suspendPolicy() {
		log.debug("method enter");
		return 0;
	}

	@Override
	public VirtualMachine virtualMachine() {
		return vm;
	}

	@Override
	public void addInstanceFilter(ObjectReference instance) {
		log.debug("method enter");

	}

	@Override
	public void addThreadFilter(ThreadReference thread) {
		log.debug("method enter");

	}

	@Override
	public Location location() {
		return location;
	}

	
	public void setBreakpoint(Breakpoint breakpoint) {
		this.breakpoint = breakpoint;
	}
	public Breakpoint getBreakpoint() {
		return breakpoint;
	}

}
