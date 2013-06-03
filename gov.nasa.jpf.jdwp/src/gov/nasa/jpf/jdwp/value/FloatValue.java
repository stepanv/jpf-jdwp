package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.vm.Fields;
import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;

public class FloatValue extends PrimitiveValue {

	private float value;

	public FloatValue(float value) {
		super(Tag.FLOAT);
		this.value = value;
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		os.writeFloat(value);
	}

	@Override
	public void push(StackFrame frame) {
		frame.pushFloat(value);
	}
	
	@Override
	public void modify(Fields fields, int index) throws InvalidObject {
		fields.setFloatValue(index, value);
	}
	
	@Override
	public void modify(StackFrame stackFrame, int slotIndex) throws InvalidObject {
		stackFrame.setLocalVariable(slotIndex, Float.floatToIntBits(value), false);
	}

}