package test.jdi.impl.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.request.BreakpointRequest;

import test.jdi.impl.EventRequestManagerImpl;
import test.jdi.impl.EventRequestManagerImpl.EventRequestContainer;
import test.jdi.impl.LocationImpl;
import test.jdi.impl.VirtualMachineImpl;
import test.jdi.impl.request.BreakpointRequestImpl;

import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;

public class BreakpointManager {

	public static final Logger log = org.apache.log4j.Logger.getLogger(BreakpointManager.class);
	
	private List<Breakpoint> breakpoints = new ArrayList<Breakpoint>();

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
	 * @param ti 
	 * @return null or Breakpoint if exists
	 */
	public Breakpoint breakpoint(Instruction instruction, ThreadInfo ti) {
		for (Breakpoint breakpoint : breakpoints) {
			if (instruction.equals(breakpoint.getInstruction())) {
				try {
					log.debug(("Breakpoint found for instruction " + instruction + " at " + breakpoint.getRequest().location().sourceName() + ":" + breakpoint.getRequest().location().lineNumber()));
					
				} catch (AbsentInformationException e) {
				}
				if (breakpoint.getRequest().isEnabled()) {
					return breakpoint;
				} else {
					log.debug("Breakpoint is disabled");
					return null;
				}
			}
		}
		return null;
	}
	
	public void remove(Breakpoint breakpoint) {
		breakpoints.remove(breakpoint);
	}

	public void add(Breakpoint breakpoint) {
		breakpoints.add(breakpoint);
	}
}
