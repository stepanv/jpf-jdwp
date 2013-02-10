package test.jdi.impl;

import gov.nasa.jpf.jvm.FieldInfo;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Field;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;

public class FieldImpl extends TypeComponentImpl implements Field {

	private FieldInfo fieldInfo;
	private ReferenceTypeImpl referenceType;

	public FieldImpl(FieldInfo fieldInfo, ReferenceTypeImpl referenceType, VirtualMachineImpl vm) {
		super(vm);
		this.fieldInfo = fieldInfo;
		
		this.referenceType = referenceType;
	}

	@Override
	public String name() {
		return fieldInfo.getName();
	}

	@Override
	public String signature() {
		return fieldInfo.getSignature();
	}

	@Override
	public String genericSignature() {
		return null; // TODO possible not ok
	}

	@Override
	public ReferenceType declaringType() {
		return referenceType;
	}

	@Override
	public boolean isStatic() {
		return fieldInfo.isStatic();
	}

	@Override
	public boolean isFinal() {
		return fieldInfo.isFinal();
	}

	@Override
	public boolean isSynthetic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int modifiers() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isPrivate() {
		return !fieldInfo.isPublic(); // TODO that's weird
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
		return fieldInfo.isPublic();
	}

	@Override
	public int compareTo(Field o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String typeName() {
		return referenceType.name();
	}

	@Override
	public Type type() throws ClassNotLoadedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTransient() {
		return fieldInfo.isTransient();
	}

	@Override
	public boolean isVolatile() {
		return fieldInfo.isVolatile();
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
