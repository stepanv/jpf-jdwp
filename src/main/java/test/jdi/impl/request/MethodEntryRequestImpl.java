package test.jdi.impl.request;

import org.apache.log4j.Logger;

import test.jdi.impl.VirtualMachineImpl;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.request.MethodEntryRequest;

public class MethodEntryRequestImpl extends EventRequestImpl implements MethodEntryRequest {

	public MethodEntryRequestImpl(VirtualMachineImpl vm) {
		super(vm);
	}

	public static final Logger log = org.apache.log4j.Logger.getLogger(MethodEntryRequestImpl.class);
	
	@Override
	public void addClassExclusionFilter(String classPattern) {
		log.debug("method entering");

	}

	@Override
	public void addClassFilter(ReferenceType refType) {
		log.debug("method entering");

	}

	@Override
	public void addClassFilter(String classPattern) {
		log.debug("method entering");

	}

	@Override
	public void addInstanceFilter(ObjectReference instance) {
		log.debug("method entering");

	}

	@Override
	public void addThreadFilter(ThreadReference thread) {
		log.debug("method entering");

	}

}
