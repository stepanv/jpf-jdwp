package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
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
	public void writeUntagged(DataOutputStream os) throws IOException {
		os.writeByte(value);
	}

	@Override
	public void push(StackFrame frame) {
		frame.push(value);
	}

	@Override
	public void modify(StackFrame stackFrame, int slotIndex) {
		stackFrame.setLocalVariable(slotIndex, value, false);
	}
	
	@Override
	public void modify(ElementInfo instance, FieldInfo field) {
		instance.setByteField(field, value);
	}
	
	@Override
	public void modify(ElementInfo arrayInstance, int index) {
		arrayInstance.setIntElement(index, value);
	}

}
