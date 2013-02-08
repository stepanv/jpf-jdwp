package test.jdi.impl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import test.jdi.impl.event.ThreadStartEventImpl;
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
import com.sun.jdi.event.ThreadStartEvent;
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

		private CopyOnWriteArrayList<R> requests = new CopyOnWriteArrayList<R>();
		private VirtualMachineImpl vm;

		private EventRequestContainer(VirtualMachineImpl vm) {
			this.vm = vm;
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

		public void dispatch() {
			boolean suspendAll = false;
			
			synchronized (requests) {
				for (EventRequest request : requests) {
					if (request.isEnabled()) {
						if (((EventRequestImpl)request).dispatch() != EventRequest.SUSPEND_NONE) {
							suspendAll = true;
						}	
					}
				}
			}
			
			// suspending outside of synchronized block
			if (suspendAll) {
				vm.jpfManager.suspendAllThreads();
			}
		}
	}
	
	VirtualMachineImpl vm;
	
	public EventRequestManagerImpl(VirtualMachineImpl vm) {
		this.vm = vm;
				
		accessWatchpointRequestContainer = new EventRequestContainer<AccessWatchpointRequest>(vm);
		breakpointRequestContainer = new EventRequestContainer<BreakpointRequest>(vm);
		classPrepareRequestContainer = new EventRequestContainer<ClassPrepareRequest>(vm);
		classUnloadRequestContainer = new EventRequestContainer<ClassUnloadRequest>(vm);
		methodEntryRequestContainer = new EventRequestContainer<MethodEntryRequest>(vm);
		methodExitRequestContainer = new EventRequestContainer<MethodExitRequest>(vm);
		exceptionRequestContainer = new EventRequestContainer<ExceptionRequest>(vm);
		modificationWatchpointRequestContainer = new EventRequestContainer<ModificationWatchpointRequest>(vm);
		stepRequestContainer = new EventRequestContainer<StepRequest>(vm);
		threadDeathRequestContainer = new EventRequestContainer<ThreadDeathRequest>(vm);
		threadStartRequestContainer = new EventRequestContainer<ThreadStartRequest>(vm);
		vmDeathRequestContainer = new EventRequestContainer<VMDeathRequest>(vm);
		monitorContendedEnteredRequestContainer = new EventRequestContainer<MonitorContendedEnteredRequest>(vm);
		monitorContendedEnterRequestContainer = new EventRequestContainer<MonitorContendedEnterRequest>(vm);
		monitorWaitedRequestContainer = new EventRequestContainer<MonitorWaitedRequest>(vm);
		monitorWaitRequestContainer = new EventRequestContainer<MonitorWaitRequest>(vm);
	}
	
	private EventRequestContainer<AccessWatchpointRequest> accessWatchpointRequestContainer;
	private EventRequestContainer<BreakpointRequest> breakpointRequestContainer;
	private EventRequestContainer<ClassPrepareRequest> classPrepareRequestContainer;
	private EventRequestContainer<ClassUnloadRequest> classUnloadRequestContainer;
	private EventRequestContainer<MethodEntryRequest> methodEntryRequestContainer;
	private EventRequestContainer<MethodExitRequest> methodExitRequestContainer;
	private EventRequestContainer<ExceptionRequest> exceptionRequestContainer;
	private EventRequestContainer<ModificationWatchpointRequest> modificationWatchpointRequestContainer;
	private EventRequestContainer<StepRequest> stepRequestContainer;
	private EventRequestContainer<ThreadDeathRequest> threadDeathRequestContainer;
	private EventRequestContainer<ThreadStartRequest> threadStartRequestContainer;
	private EventRequestContainer<VMDeathRequest> vmDeathRequestContainer;
	private EventRequestContainer<MonitorContendedEnteredRequest> monitorContendedEnteredRequestContainer;
	private EventRequestContainer<MonitorContendedEnterRequest> monitorContendedEnterRequestContainer;
	private EventRequestContainer<MonitorWaitedRequest> monitorWaitedRequestContainer;
	private EventRequestContainer<MonitorWaitRequest> monitorWaitRequestContainer;
	
	
	public static final Logger log = org.apache.log4j.Logger.getLogger(EventRequestManagerImpl.class);
	
	
	public EventRequestContainer<AccessWatchpointRequest> getAccessWatchpointRequestContainer() {
		return accessWatchpointRequestContainer;
	}
	public EventRequestContainer<BreakpointRequest> getBreakpointRequestContainer() {
		return breakpointRequestContainer;
	}
	public EventRequestContainer<ClassPrepareRequest> getClassPrepareRequestContainer() {
		return classPrepareRequestContainer;
	}
	public EventRequestContainer<ClassUnloadRequest> getClassUnloadRequestContainer() {
		return classUnloadRequestContainer;
	}
	public EventRequestContainer<MethodEntryRequest> getMethodEntryRequestContainer() {
		return methodEntryRequestContainer;
	}
	public EventRequestContainer<MethodExitRequest> getMethodExitRequestContainer() {
		return methodExitRequestContainer;
	}
	public EventRequestContainer<ExceptionRequest> getExceptionRequestContainer() {
		return exceptionRequestContainer;
	}
	public EventRequestContainer<ModificationWatchpointRequest> getModificationWatchpointRequestContainer() {
		return modificationWatchpointRequestContainer;
	}
	public EventRequestContainer<StepRequest> getStepRequestContainer() {
		return stepRequestContainer;
	}
	public EventRequestContainer<ThreadDeathRequest> getThreadDeathRequestContainer() {
		return threadDeathRequestContainer;
	}
	public EventRequestContainer<ThreadStartRequest> getThreadStartRequestContainer() {
		return threadStartRequestContainer;
	}
	public EventRequestContainer<VMDeathRequest> getVmDeathRequestContainer() {
		return vmDeathRequestContainer;
	}
	public EventRequestContainer<MonitorContendedEnteredRequest> getMonitorContendedEnteredRequestContainer() {
		return monitorContendedEnteredRequestContainer;
	}
	public EventRequestContainer<MonitorContendedEnterRequest> getMonitorContendedEnterRequestContainer() {
		return monitorContendedEnterRequestContainer;
	}
	public EventRequestContainer<MonitorWaitedRequest> getMonitorWaitedRequestContainer() {
		return monitorWaitedRequestContainer;
	}
	public EventRequestContainer<MonitorWaitRequest> getMonitorWaitRequestContainer() {
		return monitorWaitRequestContainer;
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
