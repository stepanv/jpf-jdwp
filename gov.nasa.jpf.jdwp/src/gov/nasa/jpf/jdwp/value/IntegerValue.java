package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;

public class IntegerValue extends PrimitiveValue {

	private int value;

	public IntegerValue(int value) {
		super(Tag.INT);
		this.value = value;
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		os.writeInt(value);
	}

	@Override
	public void push(StackFrame frame) {
		frame.push(value);
	}

}