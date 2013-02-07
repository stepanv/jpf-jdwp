package test.jdi.impl.internal;

import com.sun.jdi.event.MethodEntryEvent;
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
		if (vmJdi.getEventRequestManager().methodEntryRequests().size() > 0) {
			MethodEntryEvent te = new MethodEntryEventImpl(vmJdi, vm.getLastThreadInfo(), vmJdi.getEventRequestManager().methodEntryRequests().get(0), vmJdi.getJvm().getNextInstruction(), vmJdi.getJvm().getCurrentThread());
			vmJdi.addEvent(te);
		}
	}
	
	@Override
	public void threadStarted(JVM vm) {
		if (vmJdi.getEventRequestManager().threadStartRequests().size() > 0) {
			ThreadStartEvent te = new ThreadStartEventImpl(vmJdi, vm.getLastThreadInfo(), vmJdi.getEventRequestManager().threadStartRequests().get(0));
			vmJdi.addEvent(te);
		}
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
	}
	
	@Override
	public void executeInstruction(JVM vm) {
		vmJdi.started();
		jpfManager.suspendIfSuspended();
		jpfManager.handlePossibleBreakpointHit();
	}
}
