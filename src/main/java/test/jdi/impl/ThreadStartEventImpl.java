package test.jdi.impl;

import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.ThreadInfo;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.ThreadStartRequest;

public class ThreadStartEventImpl implements ThreadStartEvent {

	private VirtualMachineImpl vm;
	private ThreadInfo ti;
	private ThreadStartRequest threadStartRequest;

	public ThreadStartEventImpl(VirtualMachineImpl vmJdi, ThreadInfo ti, ThreadStartRequest threadStartRequest) {
		this.vm = vmJdi;
		this.ti = ti;
		this.threadStartRequest = threadStartRequest;
	}

	@Override
	public EventRequest request() {
		return threadStartRequest;
	}

	@Override
	public VirtualMachine virtualMachine() {
		return vm;
	}

	@Override
	public ThreadReference thread() {
		return vm.getThreads().get(ti);
	}

}
