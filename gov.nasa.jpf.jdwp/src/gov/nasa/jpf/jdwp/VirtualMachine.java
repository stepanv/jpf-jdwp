package gov.nasa.jpf.jdwp;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jdwp.event.ClassPrepareEvent;
import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.EventRequest;
import gov.nasa.jpf.jdwp.event.ThreadStartEvent;
import gov.nasa.jpf.jdwp.event.VmInitStart;
import gov.nasa.jpf.jdwp.event.filter.StepFilter;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.JVM;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class VirtualMachine {
	private JPF jpf;
	private List<ClassInfo> loadedClases = new CopyOnWriteArrayList<ClassInfo>();
	private boolean started;

	public VirtualMachine(JPF jpf) {
		this.jpf = jpf;
	}

	public void started(JVM vm, List<ClassInfo> postponedLoadedClasses) {
		if (!started) {
			started = true;
			System.out.println("About to send vm started event .. sending postponed class loads.");
			List<Event> events = new ArrayList<Event>();
			
			for (ClassInfo classInfo : postponedLoadedClasses) {
				events.add(new ClassPrepareEvent(vm.getCurrentThread(), classInfo, 0));
			}
			postponedLoadedClasses.clear();
			Jdwp.notify(events.toArray(new Event[events.size()])); // TODO according to JDWP specs classprepare events can be in a composite event only if are for the same class
			
			VmInitStart vmInitEvent = new VmInitStart(vm.getCurrentThread());
			System.out.println("Notifying about vm started");
			events.add(vmInitEvent);
			Jdwp.notify(vmInitEvent);
			System.out.println(" not suspending after start");
			//suspendAllThreads();
			
			// we also need to send thread start event
			// TODO [for PJA] is this a bug in JPF main thread start doesn't trigger threadStarted event in JPF listeners
			Jdwp.notify(new ThreadStartEvent(vm.getCurrentThread()));
			//events.add(new ThreadStartEvent(vm.getCurrentThread()));
			
		}
		
	}
	public void notifyClassLoaded(ClassInfo lastClassInfo) {
		loadedClases.add(lastClassInfo);
		
	}

	public Collection<ClassInfo> getAllLoadedClasses() {
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
	
	public void registerEventRequest(EventRequest eventRequest) {
		requests.add(eventRequest);
	}

	public JPF getJpf() {
		return jpf;
	}


}
