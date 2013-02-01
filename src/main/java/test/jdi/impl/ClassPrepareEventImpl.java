package test.jdi.impl;

import gov.nasa.jpf.jvm.ThreadInfo;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;

public class ClassPrepareEventImpl implements ClassPrepareEvent {

	private ThreadInfo ti;
	private ClassPrepareRequest request;
	private VirtualMachineImpl vm;
	private ReferenceTypeImpl reference;

	public ClassPrepareEventImpl(VirtualMachineImpl virtualMachine,
			ThreadInfo lastThreadInfo, ClassPrepareRequest request, ReferenceTypeImpl reference) {
		this.ti = lastThreadInfo;
		this.request = request;
		this.vm = virtualMachine;
		this.reference = reference;
	}

	@Override
	public EventRequest request() {
		return request;
	}

	@Override
	public VirtualMachine virtualMachine() {
		return vm;
	}

	@Override
	public ReferenceType referenceType() {
		return reference;
	}

	@Override
	public ThreadReference thread() {
		return vm.getThreads().get(ti);
	}

}
