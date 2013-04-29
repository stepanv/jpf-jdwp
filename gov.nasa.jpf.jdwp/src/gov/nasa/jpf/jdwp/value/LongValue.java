package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;

public class LongValue extends PrimitiveValue {

	private long value;

	public LongValue(long value) {
		super(Tag.LONG);
		this.value = value;
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		os.writeLong(value);
	}

	@Override
	public void push(StackFrame frame) {
		frame.pushLong(value);
	}
	
}
