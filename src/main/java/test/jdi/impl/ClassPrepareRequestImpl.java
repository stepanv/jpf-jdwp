package test.jdi.impl;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.InvalidRequestStateException;

public class ClassPrepareRequestImpl implements ClassPrepareRequest {

	public ClassPrepareRequestImpl() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void addCountFilter(int arg1) throws InvalidRequestStateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void disable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void enable() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getProperty(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void putProperty(Object key, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEnabled(boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSuspendPolicy(int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public int suspendPolicy() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public VirtualMachine virtualMachine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addClassExclusionFilter(String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addClassFilter(ReferenceType arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addClassFilter(String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addSourceNameFilter(String arg1) {
		// TODO Auto-generated method stub

	}

}
