package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
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
	public void writeUntagged(DataOutputStream os) throws IOException {
		os.writeFloat(value);
	}

	@Override
	public void push(StackFrame frame) {
		frame.pushFloat(value);
	}
	
	@Override
	public void modify(StackFrame stackFrame, int slotIndex) {
		stackFrame.setLocalVariable(slotIndex, Float.floatToIntBits(value), false);
	}
	
	@Override
	public void modify(ElementInfo instance, FieldInfo field) {
		instance.setFloatField(field, value);
	}
	
	@Override
	public void modify(ElementInfo arrayInstance, int index) {
		arrayInstance.setFloatElement(index, value);
	}

}
