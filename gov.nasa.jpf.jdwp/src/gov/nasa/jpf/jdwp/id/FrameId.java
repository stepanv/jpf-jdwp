package gov.nasa.jpf.jdwp.id;

import gov.nasa.jpf.vm.StackFrame;

public class FrameId extends Identifier<StackFrame>{

	public FrameId(long id, StackFrame object) {
		super(id, object);
		throw new RuntimeException("NOT IMPLEMENTED YET: " + object);
	}

}
