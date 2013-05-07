package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.vm.Fields;
import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;

public class ShortValue extends PrimitiveValue {

	private short value;

	public ShortValue(short value) {
		super(Tag.SHORT);
		this.value = value;
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		os.writeShort(value);
	}

	@Override
	public void push(StackFrame frame) {
		frame.push(value);
	}
	
	@Override
	public void modify(Fields fields, int index) throws InvalidObject {
		fields.setShortValue(index, value);
	}

}
