package test.jdi.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import test.jdi.impl.internal.BreakpointManager;

import com.sun.jdi.Field;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.AccessWatchpointRequest;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.ClassUnloadRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.ExceptionRequest;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.ModificationWatchpointRequest;
import com.sun.jdi.request.MonitorContendedEnterRequest;
import com.sun.jdi.request.MonitorContendedEnteredRequest;
import com.sun.jdi.request.MonitorWaitRequest;
import com.sun.jdi.request.MonitorWaitedRequest;
import com.sun.jdi.request.StepRequest;
import com.sun.jdi.request.ThreadDeathRequest;
import com.sun.jdi.request.ThreadStartRequest;
import com.sun.jdi.request.VMDeathRequest;

public class EventRequestManagerImpl implements EventRequestManager {

	private static class EventRequestType<RT extends EventRequest> {

		private ArrayList<RT> requests;

		private EventRequestType() {
			requests= new ArrayList<RT>();
		}
		
		public List<RT> getUnmodifiableList() {
			return Collections.unmodifiableList(requests);
		}
		
		public void clear() {
			requests.clear();
		}
	}
	
	private List<ThreadStartRequest> threadStartRequests = new ArrayList<ThreadStartRequest>();
	private List<ThreadDeathRequest> threadDeathRequests = new ArrayList<ThreadDeathRequest>();
	private List<MethodEntryRequest> methodEntryRequests = new ArrayList<MethodEntryRequest>();
	private List<MethodExitRequest> methodExitRequests = new ArrayList<MethodExitRequest>();
	private List<VMDeathRequest> vmDeathRequests = new ArrayList<VMDeathRequest>();
	
	
	private List<ClassPrepareRequest> classPrepareRequests = new ArrayList<ClassPrepareRequest>();
	private List<ClassUnloadRequest> classUnloadRequests = new ArrayList<ClassUnloadRequest>();
	
	public static final Logger log = org.apache.log4j.Logger.getLogger(EventRequestManagerImpl.class);
	
	VirtualMachineImpl vm;
	
	public EventRequestManagerImpl(VirtualMachineImpl vm) {
		this.vm = vm;
	}
	@Override
	public VirtualMachine virtualMachine() {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<AccessWatchpointRequest> accessWatchpointRequests() {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<BreakpointRequest> breakpointRequests() {
		log.debug("method entering");
		return null; //Collections.unmodifiableList(breakpointManager.getBreakpointRequests());
	}

	@Override
	public List<ClassPrepareRequest> classPrepareRequests() {
		return classPrepareRequests;
	}

	@Override
	public List<ClassUnloadRequest> classUnloadRequests() {
		return classUnloadRequests;
	}

	@Override
	public AccessWatchpointRequest createAccessWatchpointRequest(Field arg1) {
		log.debug("method entering");
		return null;
	}
	
	BreakpointManager breakpointManager = new BreakpointManager(this, this.vm);

	public BreakpointManager getBreakpointManager() {
		return breakpointManager;
	}
	
	@Override
	public BreakpointRequest createBreakpointRequest(Location location) {
		log.debug("method entering");
		
		return breakpointManager.createBreakpoint(location);
	}

	@Override
	public ClassPrepareRequest createClassPrepareRequest() {
		
		ClassPrepareRequest classPrepareRequest = new ClassPrepareRequestImpl(this.vm);
		synchronized (classPrepareRequests) {
			// reqests list needs to be synchronized 
			// sometimes we can iterate over requests and new request is added and then
			// concurrent change of list exception is thrown by jre
			// TODO synchronized should be all the lists
			classPrepareRequests.add(classPrepareRequest);
		}
		return classPrepareRequest;
	}

	@Override
	public ClassUnloadRequest createClassUnloadRequest() {
		ClassUnloadRequest classUnloadRequest = new ClassUnloadRequestImpl();
		classUnloadRequests.add(classUnloadRequest);
		return classUnloadRequest;
	}

	@Override
	public ExceptionRequest createExceptionRequest(ReferenceType arg1,
			boolean arg2, boolean arg3) {
		ExceptionRequest exceptionRequest = new ExceptionRequestImpl();
		return exceptionRequest;
	}

	@Override
	public MethodEntryRequest createMethodEntryRequest() {
		MethodEntryRequest methodEntryRequest = new MethodEntryRequestImpl();
		methodEntryRequests.add(methodEntryRequest);
		return methodEntryRequest;
	}

	@Override
	public MethodExitRequest createMethodExitRequest() {
		MethodExitRequest methodExitRequest = new MethodExitRequestImpl();
		methodExitRequests.add(methodExitRequest);
		return methodExitRequest;
	}

	@Override
	public MonitorContendedEnteredRequest createMonitorContendedEnteredRequest() {
		MonitorContendedEnteredRequest monitorContendedEnteredRequest = new MonitorContendedEnteredRequestImpl();
		return monitorContendedEnteredRequest;
	}

	@Override
	public MonitorContendedEnterRequest createMonitorContendedEnterRequest() {
		MonitorContendedEnterRequest monitorContendedEnterRequest = new MonitorContendedEnterRequestImpl();
		return monitorContendedEnterRequest;
	}

	@Override
	public MonitorWaitedRequest createMonitorWaitedRequest() {
		MonitorWaitedRequest monitorWaitedRequest = new MonitorWaitedRequestImpl();
		return monitorWaitedRequest;
	}

	@Override
	public MonitorWaitRequest createMonitorWaitRequest() {
		MonitorWaitRequest monitorWaitRequest = new MonitorWaitRequestImpl();
		return monitorWaitRequest;
	}

	@Override
	public ModificationWatchpointRequest createModificationWatchpointRequest(
			Field arg1) {
		ModificationWatchpointRequest modificationWatchpointRequest = new ModificationWatchpointRequestImpl();
		return modificationWatchpointRequest;
	}

	@Override
	public StepRequest createStepRequest(ThreadReference arg1, int arg2,
			int arg3) {
		StepRequest stepRequest = new StepRequestImpl();
		return stepRequest;
	}

	@Override
	public ThreadDeathRequest createThreadDeathRequest() {
		ThreadDeathRequest threadDeathRequest = new ThreadDeathRequestImpl();
		threadDeathRequests.add(threadDeathRequest);
		return threadDeathRequest;
	}

	@Override
	public ThreadStartRequest createThreadStartRequest() {
		ThreadStartRequest threadStartRequest = new ThreadStartRequestImpl();
		threadStartRequests.add(threadStartRequest);
		return threadStartRequest;
	}

	@Override
	public VMDeathRequest createVMDeathRequest() {
		VMDeathRequest vmDeathRequest = new VMDeathRequestImpl();
		vmDeathRequests.add(vmDeathRequest);
		return vmDeathRequest;
	}

	@Override
	public void deleteAllBreakpoints() {
		log.debug("method entering");

	}

	@Override
	public void deleteEventRequest(EventRequest arg1) {
		log.debug("method entering");

	}

	@Override
	public void deleteEventRequests(List<? extends EventRequest> arg1) {
		log.debug("method entering");

	}

	@Override
	public List<ExceptionRequest> exceptionRequests() {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<MethodEntryRequest> methodEntryRequests() {
		log.debug("method entering");
		return methodEntryRequests;
	}

	@Override
	public List<MethodExitRequest> methodExitRequests() {
		log.debug("method entering");
		return methodExitRequests;
	}

	@Override
	public List<ModificationWatchpointRequest> modificationWatchpointRequests() {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<StepRequest> stepRequests() {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<ThreadDeathRequest> threadDeathRequests() {
		log.debug("method entering");
		return threadDeathRequests;
	}

	@Override
	public List<ThreadStartRequest> threadStartRequests() {
		log.debug("method entering");
		return threadStartRequests;
	}

	@Override
	public List<VMDeathRequest> vmDeathRequests() {
		log.debug("method entering");
		return vmDeathRequests;
	}

	@Override
	public List<MonitorContendedEnterRequest> monitorContendedEnterRequests() {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<MonitorContendedEnteredRequest> monitorContendedEnteredRequests() {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<MonitorWaitRequest> monitorWaitRequests() {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<MonitorWaitedRequest> monitorWaitedRequests() {
		log.debug("method entering");
		return null;
	}
	
	
//	public void pairAndAddBreakpointEvent(BreakPointStatus bp) {
//		if (breakpointRequests.size() == 0) {
//			pendingBps.add(bp);
//		} else {
//			BreakpointRequestImpl bRequest = breakpointRequests.get(0);
//			BreakpointEventImpl bpEvent = new BreakpointEventImpl(bp, vm, bRequest);
//			vm.addEvent(bpEvent);
//		}
//		
//		
//	}

}
