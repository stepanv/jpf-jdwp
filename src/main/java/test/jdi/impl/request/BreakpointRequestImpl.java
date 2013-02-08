package test.jdi.impl.request;

import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;

import org.apache.log4j.Logger;

import test.jdi.impl.EventRequestManagerImpl.EventRequestContainer;
import test.jdi.impl.LocationImpl;
import test.jdi.impl.VirtualMachineImpl;
import test.jdi.impl.event.BreakpointEventImpl;
import test.jdi.impl.event.EventImpl;
import test.jdi.impl.internal.Breakpoint;
import test.jdi.impl.internal.BreakpointManager;

import com.sun.jdi.AbsentInformationException;
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
		
		this.setSuspendPolicy(SUSPEND_ALL);
		
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


	@Override
	public EventImpl conditionallyGenerateEvent(VirtualMachineImpl vm, JVM jvm) {
		ThreadInfo currentThread = jvm.getCurrentThread();
		Instruction nextInstruction = jvm.getNextInstruction();
		
		if (nextInstruction.equals(this.breakpoint.getInstruction())) {
			try {
				log.debug("Breakpoint HIT for instruction " + nextInstruction + " at " + breakpoint.getRequest().location().sourceName() + ":" + breakpoint.getRequest().location().lineNumber());
			} catch (AbsentInformationException e) {
				log.debug("Breakpoint HIT for instruction " + nextInstruction);
			}
		
			vm.getThreadManager().setIsAtBreakpoint(currentThread);
			
			return new BreakpointEventImpl(breakpoint.getRequest(), vm, currentThread);
		}
		
		return null;
	}

}
