package test.jdi.impl.request;

import org.apache.log4j.Logger;

import test.jdi.impl.EventRequestManagerImpl.EventRequestContainer;
import test.jdi.impl.VirtualMachineImpl;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.request.ExceptionRequest;

public class ExceptionRequestImpl extends EventRequestImpl implements ExceptionRequest {

	public ExceptionRequestImpl(VirtualMachineImpl vm, EventRequestContainer<ExceptionRequest> exceptionRequestContainer) {
		super(vm, exceptionRequestContainer);
	}

	public static final Logger log = org.apache.log4j.Logger.getLogger(ExceptionRequestImpl.class);
	
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
