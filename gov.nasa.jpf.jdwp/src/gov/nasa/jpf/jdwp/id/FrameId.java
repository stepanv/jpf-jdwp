package gov.nasa.jpf.jdwp.id;

import gov.nasa.jpf.jvm.StackFrame;

public class FrameId extends Identifier<StackFrame>{

	public FrameId(StackFrame object) {
		super(0, object);
		throw new RuntimeException("NOT IMPLEMENTED YET: " + object);
	}

}
