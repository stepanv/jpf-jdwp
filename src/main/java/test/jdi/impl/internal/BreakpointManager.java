package test.jdi.impl.internal;

import java.util.ArrayList;
import java.util.List;

import com.sun.jdi.Location;
import com.sun.jdi.request.BreakpointRequest;

import test.jdi.impl.BreakpointRequestImpl;
import test.jdi.impl.EventRequestManagerImpl;
import test.jdi.impl.LocationImpl;
import test.jdi.impl.VirtualMachineImpl;

import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;

public class BreakpointManager {

	private List<Breakpoint> breakpoints = new ArrayList<Breakpoint>();
	private List<BreakpointRequestImpl> breakpointRequests = new ArrayList<BreakpointRequestImpl>();

	private EventRequestManagerImpl eventRequestManager;
	private VirtualMachineImpl vm;
	
	public BreakpointManager(EventRequestManagerImpl eventRequestManagerImpl,
			VirtualMachineImpl vm) {
		this.eventRequestManager = eventRequestManagerImpl;
		this.vm = vm;
	}

	public boolean isBreakpointHit(Instruction instruction) {
		for (Breakpoint breakpoint : breakpoints) {
			if (instruction.equals(breakpoint.getInstruction())) {
				return true;
			}
		}
		return false;
	}

	public BreakpointRequest createBreakpoint(Location location) {
		
		LocationImpl locationImpl = (LocationImpl)location;
		
		BreakpointRequestImpl br = new BreakpointRequestImpl(this.vm, locationImpl);
		Breakpoint b = new Breakpoint(br, this.vm, locationImpl.getInstruction());
		br.setBreakpoint(b);
		
		breakpoints.add(b);
		breakpointRequests.add(br);
		
		return br;
	}
}
