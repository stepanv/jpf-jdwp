package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;

public class DoubleValue extends PrimitiveValue {

	private double value;

	public DoubleValue(double value) {
		super(Tag.DOUBLE);
		this.value = value;
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		os.writeDouble(value);
	}

	@Override
	public void push(StackFrame frame) {
		frame.pushDouble(value);
	}

}