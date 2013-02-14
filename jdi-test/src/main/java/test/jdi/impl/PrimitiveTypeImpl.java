package test.jdi.impl;

import com.sun.jdi.PrimitiveType;

public class PrimitiveTypeImpl extends TypeImpl implements PrimitiveType {


	public PrimitiveTypeImpl(VirtualMachineImpl vm, String name,
			String signature) {
		super(vm, name, signature);
	}

}
