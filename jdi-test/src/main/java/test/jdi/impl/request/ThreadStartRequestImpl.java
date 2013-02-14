package test.jdi.impl.request;

import gov.nasa.jpf.jvm.JVM;
import test.jdi.impl.EventRequestManagerImpl.EventRequestContainer;
import test.jdi.impl.VirtualMachineImpl;
import test.jdi.impl.event.EventImpl;
import test.jdi.impl.event.ThreadStartEventImpl;

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

	@Override
	protected EventImpl conditionallyGenerateEvent(VirtualMachineImpl vm, JVM jvm) {
		return new ThreadStartEventImpl(vm, jvm.getLastThreadInfo(), this);
	}



}
