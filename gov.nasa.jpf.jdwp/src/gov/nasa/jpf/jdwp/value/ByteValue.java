package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;

public class ByteValue extends PrimitiveValue {

	private byte value;
	public ByteValue(byte value) {
		super(Tag.BYTE);
		this.value = value;
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		os.writeByte(value);
	}

	@Override
	public void push(StackFrame frame) {
		frame.push(value);
	}

}
