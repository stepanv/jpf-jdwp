package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.vm.Fields;
import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;

public class DoubleValue extends PrimitiveValue {

	private double value;

	public DoubleValue(double value) {
		super(Tag.DOUBLE);
		this.value = value;
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		os.writeDouble(value);
	}

	@Override
	public void push(StackFrame frame) {
		frame.pushDouble(value);
	}
	
	@Override
	public void modify(Fields fields, int index) throws InvalidObject {
		fields.setDoubleValue(index, value);
	}

	@Override
	public void modify(StackFrame stackFrame, int slotIndex) throws InvalidObject {
		stackFrame.setLongLocalVariable(slotIndex, Double.doubleToLongBits(value));
		
	}
	
	

}