package test.jdi.impl;

import com.sun.jdi.Accessible;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.TypeComponent;

public class TypeComponentImpl extends MirrorImpl implements TypeComponent,Accessible {

	public TypeComponentImpl(VirtualMachineImpl vm) {
		super(vm);
	}

	@Override
	public boolean isPackagePrivate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPrivate() {
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
	public int modifiers() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ReferenceType declaringType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String genericSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFinal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStatic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSynthetic() {
		// TODO Auto-generated method stub
		return false;
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

}
