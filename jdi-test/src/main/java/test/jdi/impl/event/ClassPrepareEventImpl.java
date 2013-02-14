package test.jdi.impl.event;

import test.jdi.impl.ReferenceTypeImpl;
import test.jdi.impl.VirtualMachineImpl;
import test.jdi.impl.request.ClassPrepareRequestImpl;
import gov.nasa.jpf.jvm.ThreadInfo;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.ClassPrepareEvent;

public class ClassPrepareEventImpl extends EventImpl implements ClassPrepareEvent {

	private ThreadInfo ti;
	private ReferenceTypeImpl reference;

	public ClassPrepareEventImpl(VirtualMachineImpl virtualMachine,
			ThreadInfo lastThreadInfo, ClassPrepareRequestImpl request, ReferenceTypeImpl reference) {
		super(virtualMachine, request);
		this.ti = lastThreadInfo;
		this.reference = reference;
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
