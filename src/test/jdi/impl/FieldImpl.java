package test.jdi.impl;

import gov.nasa.jpf.jvm.FieldInfo;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Field;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;

public class FieldImpl implements Field {

	private FieldInfo fieldInfo;

	public FieldImpl(FieldInfo fieldInfo) {
		this.fieldInfo = fieldInfo;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String signature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String genericSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReferenceType declaringType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isStatic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFinal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSynthetic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public VirtualMachine virtualMachine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int modifiers() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isPrivate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPackagePrivate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPublic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int compareTo(Field o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String typeName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type type() throws ClassNotLoadedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTransient() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isVolatile() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnumConstant() {
		// TODO Auto-generated method stub
		return false;
	}

	public FieldInfo getFieldInfo() {
		return fieldInfo;
	}

}
