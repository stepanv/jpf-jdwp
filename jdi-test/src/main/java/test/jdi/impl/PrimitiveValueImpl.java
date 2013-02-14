package test.jdi.impl;

import com.sun.jdi.PrimitiveValue;

public abstract class PrimitiveValueImpl extends ValueImpl implements PrimitiveValue {

	public PrimitiveValueImpl(VirtualMachineImpl vm) {
		super(vm);
		// TODO Auto-generated constructor stub
	}

	protected abstract Object objectValue();
	/**
	 * @return Returns description of Mirror object.
	 */
	@Override
	public String toString() {
		return objectValue().toString();
	}

	@Override
	public boolean booleanValue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte byteValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public char charValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double doubleValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float floatValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int intValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long longValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short shortValue() {
		// TODO Auto-generated method stub
		return 0;
	}

}
