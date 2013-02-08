package test.jdi.impl.internal;

import java.util.List;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.VMListener;
import test.jdi.impl.VirtualMachineImpl;
import test.jdi.impl.event.MethodEntryEventImpl;
import test.jdi.impl.event.ThreadStartEventImpl;
import test.jdi.impl.request.MethodEntryRequestImpl; 
import test.jdi.impl.request.ThreadStartRequestImpl;

import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.ThreadStartRequest;

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
		
		List<MethodEntryRequest> requests = vmJdi.getEventRequestManager().methodEntryRequests();
		if (requests.size() > 0) {
			MethodEntryEvent te = new MethodEntryEventImpl(vmJdi, vm.getLastThreadInfo(), (MethodEntryRequestImpl) requests.get(0), vmJdi.getJvm().getNextInstruction(), vmJdi.getJvm().getCurrentThread());
			vmJdi.addEvent(te);
		}
	}
	
	@Override
	public void threadStarted(JVM vm) {
		List<ThreadStartRequest> requests = vmJdi.getEventRequestManager().threadStartRequests();
		if (requests.size() > 0) {
			ThreadStartEvent te = new ThreadStartEventImpl(vmJdi, vm.getLastThreadInfo(), (ThreadStartRequestImpl) requests.get(0));
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
