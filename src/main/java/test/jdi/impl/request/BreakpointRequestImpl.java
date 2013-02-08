package test.jdi.impl.request;

import java.util.List;

import org.apache.log4j.Logger;

import test.jdi.impl.EventRequestManagerImpl.EventRequestContainer;
import test.jdi.impl.LocationImpl;
import test.jdi.impl.VirtualMachineImpl;
import test.jdi.impl.internal.Breakpoint;
import test.jdi.impl.internal.BreakpointManager;

import com.sun.jdi.Location;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.request.BreakpointRequest;

public class BreakpointRequestImpl extends EventRequestImpl implements BreakpointRequest {

	public static final Logger log = org.apache.log4j.Logger.getLogger(BreakpointRequestImpl.class);
	private Location location;
	private Breakpoint breakpoint;
	private BreakpointManager breakpointManager;
	
	public BreakpointRequestImpl(VirtualMachineImpl vm, LocationImpl location, EventRequestContainer<BreakpointRequest> breakpointRequestContainer, BreakpointManager breakpointManager) {
		super(vm, breakpointRequestContainer);
		this.location = location;
		this.breakpointManager = breakpointManager;
		this.breakpoint = new Breakpoint(this, vm, location.getInstruction());
		
		breakpointManager.add(this.breakpoint);
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
	
	@Override
	public void remove() {
		super.remove();
		breakpointManager.remove(breakpoint);
	}

	
	public void setBreakpoint(Breakpoint breakpoint) {
		this.breakpoint = breakpoint;
	}
	public Breakpoint getBreakpoint() {
		return breakpoint;
	}

}
