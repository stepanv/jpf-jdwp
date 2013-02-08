package test.jdi.impl.request;

import gov.nasa.jpf.jvm.JVM;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import test.jdi.impl.EventRequestManagerImpl.EventRequestContainer;
import test.jdi.impl.VirtualMachineImpl;
import test.jdi.impl.event.EventImpl;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.ClassUnloadRequest;
import com.sun.jdi.request.EventRequest;

public abstract class EventRequestImpl implements EventRequest {

	private VirtualMachineImpl vm;
	private boolean enabled;
	private int policy = 0;
	private Map<Object, Object> properties =  new HashMap<Object, Object>();
	private EventRequestContainer<? extends EventRequest> requestContainer;

	public EventRequestImpl(VirtualMachineImpl vm, EventRequestContainer<? extends EventRequest> requestContainer) {
		this.vm = vm;
		this.requestContainer = requestContainer;
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

	public void remove() {
		requestContainer.safelyRemove(this);
	}
	
	
	/**
	 * Generates an event if request decides so.
	 * TODO should be abstract and implemented in all subtypes
	 * 
	 * @param vm
	 * @param jvm
	 * @return Event instance if request satisfied, otherwise null 
	 */
	protected EventImpl conditionallyGenerateEvent(VirtualMachineImpl vm, JVM jvm) {
		return null;
	}
	
	final public int dispatch() {
		VirtualMachineImpl vm = (VirtualMachineImpl) virtualMachine();
		JVM jvm = vm.getJvm();

		EventImpl event = conditionallyGenerateEvent(vm, jvm);
		if (event != null) {
			vm.addEvent(event);
			return policy;
		}
		return SUSPEND_NONE;
	}
	
	boolean simpleMatch(String text, String pattern) {
		String[] subPatterns = pattern.split("\\*");

		// Iterate over the cards.
		for (String subPattern : subPatterns) {
			int index = text.indexOf(subPattern);

			if (index == -1) {
				return false;
			}

			text = text.substring(index + subPattern.length());
		}

		return true;
	}

}
