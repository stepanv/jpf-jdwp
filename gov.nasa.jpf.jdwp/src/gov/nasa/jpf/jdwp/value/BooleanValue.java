package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.vm.Fields;
import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;

public class BooleanValue extends PrimitiveValue {

	private boolean value;

	public BooleanValue(boolean value) {
		super(Tag.BOOLEAN);
		this.value = value;
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		os.writeBoolean(value);
	}

	@Override
	public void push(StackFrame frame) {
		frame.push(value ? 1 : 0);
	}

	@Override
	public void modify(Fields fields, int index) throws InvalidObject {
		fields.setBooleanValue(index, value);
	}

}
