package test.jdi.impl.internal;

import gov.nasa.jpf.jvm.bytecode.Instruction;
import test.jdi.impl.VirtualMachineImpl;
import test.jdi.impl.request.BreakpointRequestImpl;

public class Breakpoint {

	private BreakpointRequestImpl br;
	private VirtualMachineImpl vm;
	private Instruction instruction;

	public Breakpoint(BreakpointRequestImpl br, VirtualMachineImpl vm, Instruction instruction) {
		this.br = br;
		this.vm = vm;
		this.instruction = instruction;
	}

	public Instruction getInstruction() {
		return instruction;
	}

	public VirtualMachineImpl getVm() {
		return vm;
	}


	public BreakpointRequestImpl getRequest() {
		return br;
	}

}
