package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.vm.Fields;
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
	
	@Override
	public void modify(Fields fields, int index) throws InvalidObject {
		fields.setLongValue(index, value);
	}
	
	@Override
	public void modify(StackFrame stackFrame, int slotIndex) throws InvalidObject {
		stackFrame.setLongLocalVariable(slotIndex, value);
	}
	
}
