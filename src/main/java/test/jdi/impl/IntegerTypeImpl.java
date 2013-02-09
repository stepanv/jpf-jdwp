package test.jdi.impl;

import gov.nasa.jpf.jvm.ClassInfo;

import com.sun.jdi.IntegerType;

public class IntegerTypeImpl extends PrimitiveTypeImpl implements IntegerType {

	public IntegerTypeImpl(VirtualMachineImpl vm) {
		super(vm, "int", "I");
	}

}
