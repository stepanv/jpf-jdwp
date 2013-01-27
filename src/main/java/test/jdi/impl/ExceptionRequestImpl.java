package test.jdi.impl;

import org.apache.log4j.Logger;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.ExceptionRequest;

public class ExceptionRequestImpl implements ExceptionRequest {

	public static final Logger log = org.apache.log4j.Logger.getLogger(ExceptionRequestImpl.class);
	
	@Override
	public void addCountFilter(int count) {
		log.debug("method enter");

	}

	@Override
	public void disable() {
		log.debug("method enter");

	}

	@Override
	public void enable() {
		log.debug("method enter");

	}

	@Override
	public Object getProperty(Object key) {
		log.debug("method enter");
		return null;
	}

	@Override
	public boolean isEnabled() {
		log.debug("method enter");
		return false;
	}

	@Override
	public void putProperty(Object key, Object value) {
		log.debug("method enter");

	}

	@Override
	public void setEnabled(boolean val) {
		log.debug("method enter");

	}

	@Override
	public void setSuspendPolicy(int policy) {
		log.debug("method enter");

	}

	@Override
	public int suspendPolicy() {
		log.debug("method enter");
		return 0;
	}

	@Override
	public VirtualMachine virtualMachine() {
		log.debug("method enter");
		return null;
	}

	@Override
	public void addClassExclusionFilter(String classPattern) {
		log.debug("method enter");

	}

	@Override
	public void addClassFilter(ReferenceType refType) {
		log.debug("method enter");

	}

	@Override
	public void addClassFilter(String classPattern) {
		log.debug("method enter");

	}

	@Override
	public void addInstanceFilter(ObjectReference instance) {
		log.debug("method enter");

	}

	@Override
	public void addThreadFilter(ThreadReference thread) {
		log.debug("method enter");

	}

	@Override
	public ReferenceType exception() {
		log.debug("method enter");
		return null;
	}

	@Override
	public boolean notifyCaught() {
		log.debug("method enter");
		return false;
	}

	@Override
	public boolean notifyUncaught() {
		log.debug("method enter");
		return false;
	}

}
