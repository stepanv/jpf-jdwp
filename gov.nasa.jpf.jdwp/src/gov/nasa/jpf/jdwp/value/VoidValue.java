package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.vm.Fields;
import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;

public class VoidValue extends PrimitiveValue {

	public VoidValue() {
		super(Tag.VOID);
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
	}

	@Override
	public void push(StackFrame frame) {
		// push nothing since we're void
	}
	
	@Override
	public void modify(Fields fields, int index) throws InvalidObject {
	}

}
