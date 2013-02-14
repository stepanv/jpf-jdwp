package gov.nasa.jpf.jdwp;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import gnu.classpath.jdwp.Jdwp;
import gnu.classpath.jdwp.event.EventRequest;
import gnu.classpath.jdwp.event.ThreadStartEvent;
import gnu.classpath.jdwp.event.VmInitEvent;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jdwp.proxy.ThreadProxy;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.JVM;

public class VirtualMachine {
	private JPF jpf;
	private List<ClassInfo> loadedClases = new CopyOnWriteArrayList<ClassInfo>();
	private boolean started;

	public VirtualMachine(JPF jpf) {
		this.jpf = jpf;
	}

	public void started(JVM vm) {
		if (!started) {
			started = true;
			VmInitEvent vmInitEvent = new VmInitEvent(vm.getCurrentThread());
			System.out.println("Notifying about vm started");
			Jdwp.notify(vmInitEvent);
			System.out.println(" not suspending after start");
			//suspendAllThreads();
			
			// we also need to send thread start event
			// TODO [for PJA] is this a bug in JPF main thread start doesn't trigger threadStarted event in JPF listeners
			Jdwp.notify(new ThreadStartEvent(vm.getCurrentThread()));
		}
		
	}
	public void notifyClassLoaded(ClassInfo lastClassInfo) {
		loadedClases.add(lastClassInfo);
		
	}

	public Collection getAllLoadedClasses() {
		return loadedClases;
	}
	
	public boolean isAllThreadsSuspended() {
		return allThreadsSuspended;
	}

	boolean allThreadsSuspended = false;
	private List<EventRequest> requests = new CopyOnWriteArrayList<EventRequest>();

	public void resumeAllThreads() {
		synchronized (this) {
			 System.out.println("RESUMING ALL THREADS");
			this.notify();
		}
	}

	public void suspendAllThreads() {
		synchronized (this) {
			try {
				allThreadsSuspended = true;
				 System.out.println("SUSPENDING ALL THREADS");
				wait();
			} catch (InterruptedException e) {
			} finally {
				allThreadsSuspended = false;
				System.out.println("ALL THREADS RESUMED");
			}
		}
	}

	public void suspendIfSuspended() {
	}
	
	public List<EventRequest> getRequests() {
		return requests;
	}

	public void registerEventRequest(EventRequest request) {
		System.out.println("Registered request: " + request);
		requests.add(request);
		
	}

	public JPF getJpf() {
		return jpf;
	}

}
