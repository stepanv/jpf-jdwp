package test.jdi.impl.request;

import test.jdi.impl.VirtualMachineImpl;

import com.sun.jdi.request.ClassUnloadRequest;

public class ClassUnloadRequestImpl extends EventRequestImpl implements ClassUnloadRequest {

	public ClassUnloadRequestImpl(VirtualMachineImpl vm) {
		super(vm);
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
