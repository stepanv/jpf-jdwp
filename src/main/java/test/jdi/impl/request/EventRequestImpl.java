package test.jdi.impl.request;

import java.util.HashMap;
import java.util.Map;

import test.jdi.impl.VirtualMachineImpl;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.EventRequest;

public abstract class EventRequestImpl implements EventRequest {

	private VirtualMachineImpl vm;
	private boolean enabled;
	private int policy = 0;
	private Map<Object, Object> properties =  new HashMap<Object, Object>();

	public EventRequestImpl(VirtualMachineImpl vm) {
		this.vm = vm;
	}
	
	@Override
	public VirtualMachine virtualMachine() {
		return vm;
	}

	@Override
	public void addCountFilter(int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disable() {
		enabled = false;

	}

	@Override
	public void enable() {
		enabled = true;
	}

	@Override
	public Object getProperty(Object key) {
		return properties.get(key);
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void putProperty(Object key, Object value) {
		properties.put(key, value);
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;

	}

	@Override
	public void setSuspendPolicy(int policy) {
		this.policy = policy;
	}

	@Override
	public int suspendPolicy() {
		return policy;
	}

}
