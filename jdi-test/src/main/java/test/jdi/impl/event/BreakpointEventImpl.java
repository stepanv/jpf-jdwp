package test.jdi.impl.event;

import gov.nasa.jpf.jvm.ThreadInfo;

import org.apache.log4j.Logger;

import test.jdi.impl.VirtualMachineImpl;
import test.jdi.impl.request.BreakpointRequestImpl;

import com.sun.jdi.Location;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.BreakpointEvent;

public class BreakpointEventImpl extends EventImpl implements BreakpointEvent {

	private ThreadInfo ti;
	
	public static final Logger log = org.apache.log4j.Logger.getLogger(BreakpointEventImpl.class);

	public BreakpointEventImpl(BreakpointRequestImpl bRequest, VirtualMachineImpl vm, ThreadInfo ti) {
		super(vm, bRequest);
		this.ti = ti;
	}

	@Override
	public ThreadReference thread() {
		log.debug("method entering");
		return vm.getThreads().get(ti);
	}

	@Override
	public Location location() {
		return ((BreakpointRequestImpl)request).location();
	}

}
