package test.jdi.impl.request;

import test.jdi.impl.EventRequestManagerImpl.EventRequestContainer;
import test.jdi.impl.VirtualMachineImpl;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.request.ThreadDeathRequest;
import com.sun.jdi.request.ThreadStartRequest;

public class ThreadStartRequestImpl extends EventRequestImpl implements ThreadStartRequest {

	public ThreadStartRequestImpl(VirtualMachineImpl vm, EventRequestContainer<ThreadDeathRequest> threadDeathRequestContainer) {
		super(vm, threadDeathRequestContainer);
	}

	@Override
	public void addThreadFilter(ThreadReference thread) {
		// TODO Auto-generated method stub
		
	}



}
