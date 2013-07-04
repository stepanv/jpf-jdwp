package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;

public class VoidValue extends PrimitiveValue {

	public VoidValue() {
		super(Tag.VOID);
	}

	@Override
	public void writeUntagged(DataOutputStream os) throws IOException {
	}

	@Override
	public void push(StackFrame frame) {
		// push nothing since we're void
	}
	
	@Override
	public void modify(StackFrame stackFrame, int slotIndex) {
	}
	
	@Override
	public void modify(ElementInfo instance, FieldInfo field) {
	}
	
	@Override
	public void modify(ElementInfo arrayInstance, int index) {
	}
}
