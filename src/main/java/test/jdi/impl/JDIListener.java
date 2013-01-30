package test.jdi.impl;

import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;

import test.jdi.impl.internal.JPFManager;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.VMListener;

public class JDIListener extends ListenerAdapter implements VMListener {

	VirtualMachineImpl vmJdi;
	JPFManager jpfManager;
	
	public JDIListener(VirtualMachineImpl vmJdi) {
		this.vmJdi = vmJdi;
		jpfManager = vmJdi.getJPFManager();
	}
	
	@Override
	public void methodEntered (JVM vm) {
		vmJdi.started();
	}
	
	@Override
	public void threadStarted(JVM vm) {
		if (vmJdi.eventRequestManager.threadStartRequests().size() > 0) {
			ThreadStartEvent te = new ThreadStartEventImpl(vmJdi, vm.getLastThreadInfo(), vmJdi.eventRequestManager.threadStartRequests().remove(0));
			vmJdi.addEvent(te);
		}
	}
	
	@Override
	public void threadTerminated(JVM vm) {
		if (vmJdi.eventRequestManager.threadDeathRequests().size() > 0) {
			ThreadDeathEvent td = new ThreadDeathEventImpl(vmJdi, vm.getLastThreadInfo(), vmJdi.eventRequestManager.threadDeathRequests().remove(0));
			vmJdi.addEvent(td);
		}
	}
	
	@Override
	public void executeInstruction(JVM vm) {
		vmJdi.started();
		jpfManager.suspendIfSuspended();
		jpfManager.handlePossibleBreakpointHit();
	}
}
