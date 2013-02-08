package test.jdi.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import test.jdi.impl.internal.BreakpointManager;
import test.jdi.impl.request.AccessWatchpointRequestImpl;
import test.jdi.impl.request.BreakpointRequestImpl;
import test.jdi.impl.request.ClassPrepareRequestImpl;
import test.jdi.impl.request.ClassUnloadRequestImpl;
import test.jdi.impl.request.EventRequestImpl;
import test.jdi.impl.request.ExceptionRequestImpl;
import test.jdi.impl.request.MethodEntryRequestImpl;
import test.jdi.impl.request.MethodExitRequestImpl;
import test.jdi.impl.request.ModificationWatchpointRequestImpl;
import test.jdi.impl.request.MonitorContendedEnterRequestImpl;
import test.jdi.impl.request.MonitorContendedEnteredRequestImpl;
import test.jdi.impl.request.MonitorWaitRequestImpl;
import test.jdi.impl.request.MonitorWaitedRequestImpl;
import test.jdi.impl.request.StepRequestImpl;
import test.jdi.impl.request.ThreadDeathRequestImpl;
import test.jdi.impl.request.ThreadStartRequestImpl;
import test.jdi.impl.request.VMDeathRequestImpl;

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

	public static class EventRequestContainer<R extends EventRequest> {

		private ArrayList<R> requests;

		private EventRequestContainer() {
			requests= new ArrayList<R>();
		}
		
		public List<R> getUnmodifiableList() {
			synchronized (requests) {
				return Collections.unmodifiableList(requests);
			}
		}
		
		public R safelyAdd(R request) {
			synchronized (requests) {
				requests.add(request);
			}
			return request;
		}
		
		public void clear() {
			synchronized (requests) {
				requests.clear();
			}
		}

		public void safelyRemove(EventRequest request) {
			synchronized (requests) {
				requests.remove(request);
			}
		}
	}
	
	private EventRequestContainer<AccessWatchpointRequest> accessWatchpointRequestContainer = new EventRequestContainer<AccessWatchpointRequest>();
	private EventRequestContainer<BreakpointRequest> breakpointRequestContainer = new EventRequestContainer<BreakpointRequest>();
	private EventRequestContainer<ClassPrepareRequest> classPrepareRequestContainer = new EventRequestContainer<ClassPrepareRequest>();
	private EventRequestContainer<ClassUnloadRequest> classUnloadRequestContainer = new EventRequestContainer<ClassUnloadRequest>();
	private EventRequestContainer<MethodEntryRequest> methodEntryRequestContainer = new EventRequestContainer<MethodEntryRequest>();
	private EventRequestContainer<MethodExitRequest> methodExitRequestContainer = new EventRequestContainer<MethodExitRequest>();
	private EventRequestContainer<ExceptionRequest> exceptionRequestContainer = new EventRequestContainer<ExceptionRequest>();
	private EventRequestContainer<ModificationWatchpointRequest> modificationWatchpointRequestContainer = new EventRequestContainer<ModificationWatchpointRequest>();
	private EventRequestContainer<StepRequest> stepRequestContainer = new EventRequestContainer<StepRequest>();
	private EventRequestContainer<ThreadDeathRequest> threadDeathRequestContainer = new EventRequestContainer<ThreadDeathRequest>();
	private EventRequestContainer<ThreadStartRequest> threadStartRequestContainer = new EventRequestContainer<ThreadStartRequest>();
	private EventRequestContainer<VMDeathRequest> vmDeathRequestContainer = new EventRequestContainer<VMDeathRequest>();
	private EventRequestContainer<MonitorContendedEnteredRequest> monitorContendedEnteredRequestContainer = new EventRequestContainer<MonitorContendedEnteredRequest>();
	private EventRequestContainer<MonitorContendedEnterRequest> monitorContendedEnterRequestContainer = new EventRequestContainer<MonitorContendedEnterRequest>();
	private EventRequestContainer<MonitorWaitedRequest> monitorWaitedRequestContainer = new EventRequestContainer<MonitorWaitedRequest>();
	private EventRequestContainer<MonitorWaitRequest> monitorWaitRequestContainer = new EventRequestContainer<MonitorWaitRequest>();
	
	
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
		return breakpointRequestContainer.getUnmodifiableList();
	}

	@Override
	public List<ClassPrepareRequest> classPrepareRequests() {
		return classPrepareRequestContainer.getUnmodifiableList();
	}

	@Override
	public List<ClassUnloadRequest> classUnloadRequests() {
		return classUnloadRequestContainer.getUnmodifiableList();
	}
	
	@Override
	public AccessWatchpointRequest createAccessWatchpointRequest(Field arg1) {
		return accessWatchpointRequestContainer.safelyAdd(new AccessWatchpointRequestImpl(vm, accessWatchpointRequestContainer));
	}
	
	BreakpointManager breakpointManager = new BreakpointManager(this, vm);

	public BreakpointManager getBreakpointManager() {
		return breakpointManager;
	}
	
	@Override
	public BreakpointRequest createBreakpointRequest(Location location) {
		return breakpointRequestContainer.safelyAdd(new BreakpointRequestImpl(this.vm, (LocationImpl)location, breakpointRequestContainer, breakpointManager));
	}

	@Override
	public ClassPrepareRequest createClassPrepareRequest() {
		return classPrepareRequestContainer.safelyAdd(new ClassPrepareRequestImpl(vm, classPrepareRequestContainer));
	}

	@Override
	public ClassUnloadRequest createClassUnloadRequest() {
		return classUnloadRequestContainer.safelyAdd(new ClassUnloadRequestImpl(vm, classUnloadRequestContainer));
	}

	@Override
	public ExceptionRequest createExceptionRequest(ReferenceType arg1,
			boolean arg2, boolean arg3) {
		return exceptionRequestContainer.safelyAdd(new ExceptionRequestImpl(vm, exceptionRequestContainer));
	}

	@Override
	public MethodEntryRequest createMethodEntryRequest() {
		return methodEntryRequestContainer.safelyAdd(new MethodEntryRequestImpl(vm, methodEntryRequestContainer));
	}

	@Override
	public MethodExitRequest createMethodExitRequest() {
		return methodExitRequestContainer.safelyAdd(new MethodExitRequestImpl(vm, methodExitRequestContainer));
	}

	@Override
	public MonitorContendedEnteredRequest createMonitorContendedEnteredRequest() {
		return monitorContendedEnteredRequestContainer.safelyAdd(new MonitorContendedEnteredRequestImpl(vm, monitorContendedEnteredRequestContainer));
	}

	@Override
	public MonitorContendedEnterRequest createMonitorContendedEnterRequest() {
		return monitorContendedEnterRequestContainer.safelyAdd(new MonitorContendedEnterRequestImpl(vm, monitorContendedEnterRequestContainer));
	}

	@Override
	public MonitorWaitedRequest createMonitorWaitedRequest() {
		return monitorWaitedRequestContainer.safelyAdd(new MonitorWaitedRequestImpl(vm, monitorWaitedRequestContainer));
	}

	@Override
	public MonitorWaitRequest createMonitorWaitRequest() {
		return monitorWaitRequestContainer.safelyAdd(new MonitorWaitRequestImpl(vm, monitorWaitRequestContainer));
	}

	@Override
	public ModificationWatchpointRequest createModificationWatchpointRequest(
			Field arg1) {
		return modificationWatchpointRequestContainer.safelyAdd(new ModificationWatchpointRequestImpl(vm, modificationWatchpointRequestContainer));
	}

	@Override
	public StepRequest createStepRequest(ThreadReference arg1, int arg2,
			int arg3) {
		return stepRequestContainer.safelyAdd(new StepRequestImpl(vm, stepRequestContainer));
	}

	@Override
	public ThreadDeathRequest createThreadDeathRequest() {
		return threadDeathRequestContainer.safelyAdd(new ThreadDeathRequestImpl(vm, threadDeathRequestContainer));
	}

	@Override
	public ThreadStartRequest createThreadStartRequest() {
		return threadStartRequestContainer.safelyAdd(new ThreadStartRequestImpl(vm, threadDeathRequestContainer));
	}

	@Override
	public VMDeathRequest createVMDeathRequest() {
		return vmDeathRequestContainer.safelyAdd(new VMDeathRequestImpl(vm, vmDeathRequestContainer));
	}

	@Override
	public void deleteAllBreakpoints() {
		log.debug("method entering");

	}

	@Override
	public void deleteEventRequest(EventRequest arg1) {
		log.debug("method entering");
		
		((EventRequestImpl)arg1).remove();
	}

	@Override
	public void deleteEventRequests(List<? extends EventRequest> arg1) {
		log.debug("method entering");
		for (EventRequest request : arg1) {
			this.deleteEventRequest(request);
		}

	}

	@Override
	public List<ExceptionRequest> exceptionRequests() {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<MethodEntryRequest> methodEntryRequests() {
		return methodEntryRequestContainer.getUnmodifiableList();
	}

	@Override
	public List<MethodExitRequest> methodExitRequests() {
		return methodExitRequestContainer.getUnmodifiableList();
	}

	@Override
	public List<ModificationWatchpointRequest> modificationWatchpointRequests() {
		return modificationWatchpointRequestContainer.getUnmodifiableList();
	}

	@Override
	public List<StepRequest> stepRequests() {
		return stepRequestContainer.getUnmodifiableList();
	}

	@Override
	public List<ThreadDeathRequest> threadDeathRequests() {
		return threadDeathRequestContainer.getUnmodifiableList();
	}

	@Override
	public List<ThreadStartRequest> threadStartRequests() {
		return threadStartRequestContainer.getUnmodifiableList();
	}

	@Override
	public List<VMDeathRequest> vmDeathRequests() {
		return vmDeathRequestContainer.getUnmodifiableList();
	}

	@Override
	public List<MonitorContendedEnterRequest> monitorContendedEnterRequests() {
		return monitorContendedEnterRequestContainer.getUnmodifiableList();
	}

	@Override
	public List<MonitorContendedEnteredRequest> monitorContendedEnteredRequests() {
		return monitorContendedEnteredRequestContainer.getUnmodifiableList();
	}

	@Override
	public List<MonitorWaitRequest> monitorWaitRequests() {
		return monitorWaitRequestContainer.getUnmodifiableList();
	}

	@Override
	public List<MonitorWaitedRequest> monitorWaitedRequests() {
		return monitorWaitedRequestContainer.getUnmodifiableList();
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
