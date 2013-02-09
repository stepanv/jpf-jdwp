package test.jdi.impl;

import com.sun.jdi.IntegerValue;
import com.sun.jdi.Type;

public class IntegerValueImpl extends PrimitiveValueImpl implements
		IntegerValue {

	private IntegerTypeImpl type;
	private Integer value;

	public IntegerValueImpl(VirtualMachineImpl vm, Integer value) {
		super(vm);
		
		this.type = new IntegerTypeImpl(vm);
		this.value = value;
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compareTo(IntegerValue o) {
		return value.compareTo(o.value());
	}

	@Override
	public int value() {
		return value();
	}

	@Override
	public Type type() {
		return type;
	}

	@Override
	protected Object objectValue() {
		return value;
	}
	
}
