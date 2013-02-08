package test.jdi.impl.internal;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.VMListener;
import test.jdi.impl.EventRequestManagerImpl;
import test.jdi.impl.VirtualMachineImpl;

public class JDIListener extends ListenerAdapter implements VMListener {

	VirtualMachineImpl vmJdi;
	JPFManager jpfManager;
	ClassesManager classesManager;
	private EventRequestManagerImpl eventRequestManager;
	
	public JDIListener(VirtualMachineImpl vmJdi) {
		this.vmJdi = vmJdi;
		jpfManager = vmJdi.getJPFManager();
		classesManager = vmJdi.getClassesManager();
		eventRequestManager = vmJdi.getEventRequestManager();
	}
	
	@Override
	public void methodEntered (JVM vm) {
		vmJdi.started();
		eventRequestManager.getMethodEntryRequestContainer().dispatch();
	}
	
	@Override
	public void threadStarted(JVM vm) {
		eventRequestManager.getThreadStartRequestContainer().dispatch();
	}
	
	@Override
	public void threadTerminated(JVM vm) {
		// TODO [for PJA] there is not relation 1:1 between thread start and thread death events. (e.g. one thread can die multiple times) and JDI doesn't know what to do about that.
		if (vmJdi.getEventRequestManager().threadDeathRequests().size() > 0) {
			//ThreadDeathEvent td = new ThreadDeathEventImpl(vmJdi, vm.getLastThreadInfo(), vmJdi.getEventRequestManager().threadDeathRequests().get(0));
			//vmJdi.addEvent(td);
		}
	}
	
	@Override
	public void classLoaded(JVM vm) {
		classesManager.notifyClassLoadded(vm.getLastClassInfo());
		eventRequestManager.getClassPrepareRequestContainer().dispatch();
	}
	
	@Override
	public void executeInstruction(JVM vm) {
		vmJdi.started();
		jpfManager.suspendIfSuspended();
		eventRequestManager.getBreakpointRequestContainer().dispatch();
	}
}
