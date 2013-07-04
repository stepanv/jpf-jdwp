package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class implements corresponding primitive <i>value</i> common data type
 * of tag <i>void</i> (implemented as {@link Tag#VOID}) according to the
 * JDWP Specification as stated in the table of Detailed Command Information
 * section.
 * 
 * @see Tag
 * @see PrimitiveValue
 * 
 * @author stepan
 * 
 */
public class VoidValue extends PrimitiveValue {

	public VoidValue() {
		super(Tag.VOID);
	}

	@Override
	public void writeUntagged(DataOutputStream os) throws IOException {
		// write nothing since we're void
	}

	@Override
	public void push(StackFrame frame) {
		// push nothing since we're void
	}

	@Override
	public void modify(StackFrame stackFrame, int slotIndex) {
		// modify nothing since we're void
	}

	@Override
	public void modify(ElementInfo instance, FieldInfo field) {
		// modify nothing since we're void
	}

	@Override
	public void modify(ElementInfo arrayInstance, int index) {
		// modify nothing since we're void
	}
}
