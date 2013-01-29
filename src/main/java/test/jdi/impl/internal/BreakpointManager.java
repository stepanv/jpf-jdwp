package test.jdi.impl.internal;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.jdi.AbsentInformationException;
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

	public static final Logger log = org.apache.log4j.Logger.getLogger(BreakpointManager.class);
	
	private List<Breakpoint> breakpoints = new ArrayList<Breakpoint>();
	private List<BreakpointRequestImpl> breakpointRequests = new ArrayList<BreakpointRequestImpl>();

	public List<BreakpointRequestImpl> getBreakpointRequests() {
		return breakpointRequests;
	}

	private EventRequestManagerImpl eventRequestManager;
	private VirtualMachineImpl vm;
	
	public BreakpointManager(EventRequestManagerImpl eventRequestManagerImpl,
			VirtualMachineImpl vm) {
		this.eventRequestManager = eventRequestManagerImpl;
		this.vm = vm;
	}

	/**
	 * If breakpoint is managed for the instruction
	 * @param instruction
	 * @return null or Breakpoint if exists
	 */
	public Breakpoint breakpoint(Instruction instruction) {
		for (Breakpoint breakpoint : breakpoints) {
			if (instruction.equals(breakpoint.getInstruction())) {
				try {
					log.debug(("Breakpoint found for instruction " + instruction + " at " + breakpoint.getBr().location().sourceName() + ":" + breakpoint.getBr().location().lineNumber()));
				} catch (AbsentInformationException e) {
				}
				return breakpoint;
			}
		}
		return null;
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
