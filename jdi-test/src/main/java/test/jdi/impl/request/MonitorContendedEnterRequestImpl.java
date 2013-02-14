package test.jdi.impl.request;

import test.jdi.impl.EventRequestManagerImpl.EventRequestContainer;
import test.jdi.impl.VirtualMachineImpl;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.request.MonitorContendedEnterRequest;

public class MonitorContendedEnterRequestImpl extends EventRequestImpl implements
		MonitorContendedEnterRequest {

	public MonitorContendedEnterRequestImpl(VirtualMachineImpl vm, EventRequestContainer<MonitorContendedEnterRequest> monitorContendedEnterRequestContainer) {
		super(vm, monitorContendedEnterRequestContainer);
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
