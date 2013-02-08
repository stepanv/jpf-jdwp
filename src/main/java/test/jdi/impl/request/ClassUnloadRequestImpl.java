package test.jdi.impl.request;

import test.jdi.impl.EventRequestManagerImpl.EventRequestContainer;
import test.jdi.impl.VirtualMachineImpl;

import com.sun.jdi.request.ClassUnloadRequest;
import com.sun.jdi.request.EventRequest;

public class ClassUnloadRequestImpl extends EventRequestImpl implements ClassUnloadRequest {

	public ClassUnloadRequestImpl(VirtualMachineImpl vm, EventRequestContainer<? extends EventRequest> classUnloadRequestContainer) {
		super(vm, classUnloadRequestContainer);
	}

	@Override
	public void addClassExclusionFilter(String classPattern) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addClassFilter(String classPattern) {
		// TODO Auto-generated method stub

	}

}
