package test.jdi.impl.request;

import test.jdi.impl.VirtualMachineImpl;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.request.MonitorContendedEnteredRequest;

public class MonitorContendedEnteredRequestImpl extends EventRequestImpl implements
		MonitorContendedEnteredRequest {

	public MonitorContendedEnteredRequestImpl(VirtualMachineImpl vm) {
		super(vm);
	}

	@Override
	public void addClassExclusionFilter(String classPattern) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addClassFilter(ReferenceType refType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addClassFilter(String classPattern) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addInstanceFilter(ObjectReference instance) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addThreadFilter(ThreadReference thread) {
		// TODO Auto-generated method stub

	}

}
