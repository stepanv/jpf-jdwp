package gov.nasa.jpf.jdwp.type;

import gov.nasa.jpf.jvm.bytecode.Instruction;

public class Location {

	private Instruction instruction;

	public Location(Instruction instruction) {
		this.instruction = instruction;
	}

	public Instruction getInstruction() {
		return instruction;
	}

}
