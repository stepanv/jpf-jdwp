package test.jdi.impl.request;

import test.jdi.impl.VirtualMachineImpl;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.request.ThreadStartRequest;

public class ThreadStartRequestImpl extends EventRequestImpl implements ThreadStartRequest {

	public ThreadStartRequestImpl(VirtualMachineImpl vm) {
		super(vm);
	}

	@Override
	public void addThreadFilter(ThreadReference thread) {
		// TODO Auto-generated method stub
		
	}



}
