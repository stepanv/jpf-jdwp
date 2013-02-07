package test.jdi.impl.request;

import org.apache.log4j.Logger;

import test.jdi.impl.VirtualMachineImpl;
import test.jdi.impl.internal.Breakpoint;

import com.sun.jdi.Location;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.request.BreakpointRequest;

public class BreakpointRequestImpl extends EventRequestImpl implements BreakpointRequest {

	public static final Logger log = org.apache.log4j.Logger.getLogger(BreakpointRequestImpl.class);
	private Location location;
	private Breakpoint breakpoint;
	
	public BreakpointRequestImpl(VirtualMachineImpl vm, Location location) {
		super(vm);
		this.location = location;
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
