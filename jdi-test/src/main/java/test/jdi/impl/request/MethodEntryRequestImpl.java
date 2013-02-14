package test.jdi.impl.request;

import gov.nasa.jpf.jvm.JVM;

import org.apache.log4j.Logger;

import test.jdi.impl.EventRequestManagerImpl.EventRequestContainer;
import test.jdi.impl.VirtualMachineImpl;
import test.jdi.impl.event.EventImpl;
import test.jdi.impl.event.MethodEntryEventImpl;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.request.MethodEntryRequest;

public class MethodEntryRequestImpl extends EventRequestImpl implements MethodEntryRequest {

	public MethodEntryRequestImpl(VirtualMachineImpl vm, EventRequestContainer<MethodEntryRequest> methodEntryRequestContainer) {
		super(vm, methodEntryRequestContainer);
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

	@Override
	protected EventImpl conditionallyGenerateEvent(VirtualMachineImpl vm, JVM jvm) {
		return new MethodEntryEventImpl(vm, this, jvm.getNextInstruction(), jvm.getCurrentThread());
	}

}
