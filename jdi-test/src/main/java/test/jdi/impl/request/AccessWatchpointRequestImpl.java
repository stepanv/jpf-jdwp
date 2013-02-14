package test.jdi.impl.request;

import test.jdi.impl.EventRequestManagerImpl.EventRequestContainer;
import test.jdi.impl.VirtualMachineImpl;

import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.request.AccessWatchpointRequest;
import com.sun.jdi.request.EventRequest;

public class AccessWatchpointRequestImpl extends EventRequestImpl implements
		AccessWatchpointRequest {

	public AccessWatchpointRequestImpl(VirtualMachineImpl vm,
			EventRequestContainer<? extends EventRequest> requestContainer) {
		super(vm, requestContainer);
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

	@Override
	public Field field() {
		// TODO Auto-generated method stub
		return null;
	}

}
