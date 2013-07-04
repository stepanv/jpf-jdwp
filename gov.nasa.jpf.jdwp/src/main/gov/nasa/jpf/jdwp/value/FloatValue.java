package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class implements corresponding primitive <i>value</i> common data type
 * of tag <i>float</i> (implemented as {@link Tag#FLOAT}) according to the
 * JDWP Specification as stated in the table of Detailed Command Information
 * section.
 * 
 * @see Tag
 * @see PrimitiveValue
 * 
 * @author stepan
 * 
 */
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
