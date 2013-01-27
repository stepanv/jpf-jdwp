package test.jdi.impl;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.VMDeathRequest;

public class VMDeathRequestImpl implements VMDeathRequest {

	@Override
	public void addCountFilter(int count) {
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
	public void setEnabled(boolean val) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSuspendPolicy(int policy) {
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

}
