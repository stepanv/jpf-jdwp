package test.jdi.impl;

import gov.nasa.jpf.jvm.ThreadInfo;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.ThreadDeathRequest;

public class ThreadDeathEventImpl implements ThreadDeathEvent {

	private ThreadDeathRequest threadDeathRequest;
	private ThreadInfo ti;
	private VirtualMachineImpl vm;

	public ThreadDeathEventImpl(VirtualMachineImpl vmJdi, ThreadInfo ti, ThreadDeathRequest threadDeathRequest) {
		this.vm = vmJdi;
		this.ti = ti;
		this.threadDeathRequest = threadDeathRequest;
	}
	@Override
	public EventRequest request() {
		return threadDeathRequest;
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
