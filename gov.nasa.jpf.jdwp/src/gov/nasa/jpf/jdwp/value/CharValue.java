package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;

public class CharValue extends PrimitiveValue {

	private char value;

	public CharValue(char value) {
		super(Tag.CHAR);
		this.value = value;
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		os.writeChar(value);
	}

	@Override
	public void push(StackFrame frame) {
		frame.push(value);
	}

}
