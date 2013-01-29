package test.jdi.impl.internal;

import org.apache.log4j.Logger;

import test.jdi.impl.BreakpointEventImpl;
import test.jdi.impl.BreakpointRequestImpl;
import test.jdi.impl.VirtualMachineImpl;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;

public class JPFManager {

	public static final Logger log = org.apache.log4j.Logger
			.getLogger(JPFManager.class);

	private VirtualMachineImpl vm;

	public JPFManager(VirtualMachineImpl virtualMachineImpl) {
		this.vm = virtualMachineImpl;
	}

	public void handlePossibleBreakpointHit() {
		JVM jvm = vm.getJvm();
		ThreadInfo ti = jvm.getCurrentThread();
		Instruction instruction = jvm.getNextInstruction();

		Breakpoint breakpoint = vm.getEventRequestManager().getBreakpointManager()
				.breakpoint(instruction);
		
		if (breakpoint != null) {
			log.debug("Breakpoint hit .. supspending all threads");
			
			vm.addEvent(new BreakpointEventImpl(breakpoint.getBr(), vm));
			
			suspendAllThreads();
		}

	}

	boolean allThreadsSuspended = false;

	public void resumeAllThreads() {
		synchronized (this) {
			this.notify();
		}
	}

	public void suspendAllThreads() {
		synchronized (this) {
			try {
				allThreadsSuspended = true;
				wait();
			} catch (InterruptedException e) {
			} finally {
				allThreadsSuspended = false;
			}
		}
	}

	public void suspendIfSuspended() {
	}

}
