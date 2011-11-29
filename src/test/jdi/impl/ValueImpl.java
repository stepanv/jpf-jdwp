package test.jdi.impl;

import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.FieldInfo;

import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

public class ValueImpl implements Value {

	private ElementInfo ei;
	private FieldInfo fi;

	public ValueImpl(ElementInfo ei, FieldInfo ffi) {
		this.ei = ei;
		this.fi = ffi;
	}

	@Override
	public VirtualMachine virtualMachine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type type() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String toString() {
		return "" + ei.getIntField(fi.getName());
	}

}
