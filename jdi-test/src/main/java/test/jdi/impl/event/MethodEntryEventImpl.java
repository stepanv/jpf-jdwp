package test.jdi.impl.event;

import test.jdi.impl.LocationImpl;
import test.jdi.impl.MethodImpl;
import test.jdi.impl.ReferenceTypeImpl;
import test.jdi.impl.ThreadReferenceImpl;
import test.jdi.impl.VirtualMachineImpl;
import test.jdi.impl.request.MethodEntryRequestImpl;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;

import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.MethodEntryEvent;

public class MethodEntryEventImpl extends EventImpl implements MethodEntryEvent {

	private Instruction instruction;
	private ThreadInfo ti;

	public MethodEntryEventImpl(VirtualMachineImpl vmJdi,
			MethodEntryRequestImpl methodEntryRequest, Instruction instruction, ThreadInfo ti) {
		super(vmJdi, methodEntryRequest);
		this.instruction = instruction;
		this.ti = ti;
	}

	@Override
	public ThreadReference thread() {
		return ThreadReferenceImpl.factory(ti, vm);
	}

	@Override
	public Location location() {
		return LocationImpl.factory(instruction, ReferenceTypeImpl.factory(instruction.getMethodInfo().getClassInfo(), vm), vm);
	}

	@Override
	public Method method() {
		return MethodImpl.factory(instruction.getMethodInfo(), vm);
	}

}
