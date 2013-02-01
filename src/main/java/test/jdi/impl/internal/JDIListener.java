package test.jdi.impl.internal;

import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;

import test.jdi.impl.ThreadDeathEventImpl;
import test.jdi.impl.ThreadStartEventImpl;
import test.jdi.impl.VirtualMachineImpl;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.VMListener;

public class JDIListener extends ListenerAdapter implements VMListener {

	VirtualMachineImpl vmJdi;
	JPFManager jpfManager;
	ClassesManager classesManager;
	
	public JDIListener(VirtualMachineImpl vmJdi) {
		this.vmJdi = vmJdi;
		jpfManager = vmJdi.getJPFManager();
		classesManager = vmJdi.getClassesManager();
	}
	
	@Override
	public void methodEntered (JVM vm) {
		vmJdi.started();
	}
	
	@Override
	public void threadStarted(JVM vm) {
		if (vmJdi.getEventRequestManager().threadStartRequests().size() > 0) {
			ThreadStartEvent te = new ThreadStartEventImpl(vmJdi, vm.getLastThreadInfo(), vmJdi.getEventRequestManager().threadStartRequests().remove(0));
			vmJdi.addEvent(te);
		}
	}
	
	@Override
	public void threadTerminated(JVM vm) {
		if (vmJdi.getEventRequestManager().threadDeathRequests().size() > 0) {
			ThreadDeathEvent td = new ThreadDeathEventImpl(vmJdi, vm.getLastThreadInfo(), vmJdi.getEventRequestManager().threadDeathRequests().remove(0));
			vmJdi.addEvent(td);
		}
	}
	
	@Override
	public void classLoaded(JVM vm) {
		classesManager.notifyClassLoadded(vm.getLastClassInfo());
	}
	
	@Override
	public void executeInstruction(JVM vm) {
		vmJdi.started();
		jpfManager.suspendIfSuspended();
		jpfManager.handlePossibleBreakpointHit();
	}
}
