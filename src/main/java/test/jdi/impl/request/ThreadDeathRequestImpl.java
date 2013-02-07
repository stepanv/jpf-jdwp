package test.jdi.impl.request;

import test.jdi.impl.VirtualMachineImpl;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.ThreadDeathRequest;

public class ThreadDeathRequestImpl extends EventRequestImpl implements ThreadDeathRequest {


	public ThreadDeathRequestImpl(VirtualMachineImpl vm) {
		super(vm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addThreadFilter(ThreadReference thread) {
		// TODO Auto-generated method stub

	}

}
