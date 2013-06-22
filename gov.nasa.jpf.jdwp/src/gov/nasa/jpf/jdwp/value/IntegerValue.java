package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.vm.Fields;
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
	public void writeUntagged(DataOutputStream os) throws IOException {
		os.writeInt(value);
	}

	@Override
	public void push(StackFrame frame) {
		frame.push(value);
	}
	
	@Override
	public void modify(Fields fields, int index) {
		fields.setIntValue(index, value);
	}

	@Override
	public void modify(StackFrame stackFrame, int slotIndex) throws InvalidObject {
		stackFrame.setLocalVariable(slotIndex, value, false);
	}


}