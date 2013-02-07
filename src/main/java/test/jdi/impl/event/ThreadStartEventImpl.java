package test.jdi.impl.event;

import test.jdi.impl.VirtualMachineImpl;
import test.jdi.impl.request.ThreadStartRequestImpl;
import gov.nasa.jpf.jvm.ThreadInfo;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.ThreadStartEvent;

public class ThreadStartEventImpl extends EventImpl implements ThreadStartEvent {

	private ThreadInfo ti;

	public ThreadStartEventImpl(VirtualMachineImpl vmJdi, ThreadInfo ti, ThreadStartRequestImpl threadStartRequest) {
		super(vmJdi, threadStartRequest);
		this.ti = ti;
	}

	@Override
	public ThreadReference thread() {
		return vm.getThreads().get(ti);
	}

}
