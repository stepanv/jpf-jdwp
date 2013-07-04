package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class implements corresponding primitive <i>value</i> common data type
 * of tag <i>char</i> (implemented as {@link Tag#CHAR}) according to the
 * JDWP Specification as stated in the table of Detailed Command Information
 * section.
 * 
 * @see Tag
 * @see PrimitiveValue
 * 
 * @author stepan
 * 
 */
public class CharValue extends PrimitiveValue {

	private char value;

	public CharValue(char value) {
		super(Tag.CHAR);
		this.value = value;
	}

	@Override
	public void writeUntagged(DataOutputStream os) throws IOException {
		os.writeChar(value);
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
		instance.setCharField(field, value);
	}

	@Override
	public void modify(ElementInfo arrayInstance, int index) {
		arrayInstance.setCharElement(index, value);
	}
}
