package test.jdi.impl;

import com.sun.jdi.Type;

public class TypeImpl extends MirrorImpl implements Type {

	private String name;
	private String signature;


	public TypeImpl(VirtualMachineImpl vm, String name, String signature) {
		super(vm);
		this.name = name;
		this.signature = signature;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String signature() {
		return signature;
	}

}
