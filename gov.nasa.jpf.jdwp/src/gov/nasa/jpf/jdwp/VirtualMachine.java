package gov.nasa.jpf.jdwp;

import gnu.classpath.jdwp.Jdwp;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jdwp.event.ClassPrepareEvent;
import gov.nasa.jpf.jdwp.event.EventBase;
import gov.nasa.jpf.jdwp.event.EventRequest;
import gov.nasa.jpf.jdwp.event.ThreadStartEvent;
import gov.nasa.jpf.jdwp.event.VmStartEvent;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.VM;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class VirtualMachine {
	private JPF jpf;
	private List<ClassInfo> loadedClases = new CopyOnWriteArrayList<ClassInfo>();
	private boolean started;

	public boolean isStarted() {
		return started;
	}

	public VirtualMachine(JPF jpf) {
		this.jpf = jpf;
	}

	public void started(VM vm, List<ClassInfo> postponedLoadedClasses) {
		if (!started) {
			started = true;
			System.out.println("About to send vm started event .. sending postponed class loads.");
			List<EventBase> events = new ArrayList<EventBase>();

			for (ClassInfo classInfo : postponedLoadedClasses) {
				events.add(new ClassPrepareEvent(vm.getCurrentThread(), classInfo, 0));
			}
			postponedLoadedClasses.clear();
			Jdwp.notify(events.toArray(new EventBase[events.size()])); // TODO
																		// according
																		// to
																		// JDWP
																		// specs
																		// classprepare
																		// events
																		// can
																		// be in
																		// a
																		// composite
																		// event
																		// only
																		// if
																		// are
																		// for
																		// the
																		// same
																		// class

			VmStartEvent vmInitEvent = new VmStartEvent(vm.getCurrentThread());
			System.out.println("Notifying about vm started");
			events.add(vmInitEvent);
			Jdwp.notify(vmInitEvent);
			System.out.println(" not suspending after start");
			// suspendAllThreads();

			// we also need to send thread start event
			// TODO [for PJA] is this a bug in JPF main thread start doesn't
			// trigger threadStarted event in JPF listeners
			Jdwp.notify(new ThreadStartEvent(vm.getCurrentThread()));
			// events.add(new ThreadStartEvent(vm.getCurrentThread()));

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
		// TODO throw an exception if error occured
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

	public static class Capabilities {

		/**
		 * Can the VM watch field modification, and therefore can it send the
		 * Modification Watchpoint Event?
		 */
		public static final boolean CAN_WATCH_FIELD_MODIFICATION = true;

		/**
		 * Can the VM watch field access, and therefore can it send the Access
		 * Watchpoint Event?
		 */
		public static final boolean CAN_WATCH_FIELD_ACCESS = true; 

		/** Can the VM get the bytecodes of a given method? */
		public static final boolean CAN_GET_BYTECODES = false;

		/**
		 * Can the VM determine whether a field or method is synthetic? (that
		 * is, can the VM determine if the method or the field was invented by
		 * the compiler?)
		 */
		public static final boolean CAN_GET_SYNTHETIC_ATTRIBUTE = false;

		/** Can the VM get the owned monitors infornation for a thread? */
		public static final boolean CAN_GET_OWNED_MONITOR_INFO = false;

		/** Can the VM get the current contended monitor of a thread? */
		public static final boolean CAN_GET_CURRENT_CONTENDED_MONITOR = false;

		/** Can the VM get the monitor information for a given object? */
		public static final boolean CAN_GET_MONITOR_INFO = false;
	}

	public static class CapabilitiesNew {

		/**
		 * Can the VM watch field modification, and therefore can it send the
		 * Modification Watchpoint Event?
		 */
		public static final boolean CAN_WATCH_FIELD_MODIFICATION = Capabilities.CAN_WATCH_FIELD_MODIFICATION;

		/**
		 * Can the VM watch field access, and therefore can it send the Access
		 * Watchpoint Event?
		 */
		public static final boolean CAN_WATCH_FIELD_ACCESS = Capabilities.CAN_WATCH_FIELD_ACCESS;

		/** Can the VM get the bytecodes of a given method? */
		public static final boolean CAN_GET_BYTECODES = Capabilities.CAN_GET_BYTECODES;

		/**
		 * Can the VM determine whether a field or method is synthetic? (that
		 * is, can the VM determine if the method or the field was invented by
		 * the compiler?)
		 */
		public static final boolean CAN_GET_SYNTHETIC_ATTRIBUTE = Capabilities.CAN_GET_SYNTHETIC_ATTRIBUTE;

		/** Can the VM get the owned monitors infornation for a thread? */
		public static final boolean CAN_GET_OWNED_MONITOR_INFO = Capabilities.CAN_GET_OWNED_MONITOR_INFO;

		/** Can the VM get the current contended monitor of a thread? */
		public static final boolean CAN_GET_CURRENT_CONTENDED_MONITOR = Capabilities.CAN_GET_CURRENT_CONTENDED_MONITOR;

		/** Can the VM get the monitor information for a given object? */
		public static final boolean CAN_GET_MONITOR_INFO = Capabilities.CAN_GET_MONITOR_INFO;

		/** Can the VM redefine classes? */
		public static final boolean CAN_REDEFINE_CLASSES = false;

		/** Can the VM add methods when redefining classes? */
		public static final boolean CAN_ADD_METHOD = false;

		/** Can the VM redefine classesin arbitrary ways? */
		public static final boolean CAN_UNRESTRICTEDLY_REDEFINE_CLASSES = false;

		/** Can the VM pop stack frames? */
		public static final boolean CAN_POP_FRAMES = false;

		/** Can the VM filter events by specific object? */
		public static final boolean CAN_USE_INSTANCE_FILTERS = false;

		/** Can the VM get the source debug extension? */
		public static final boolean CAN_GET_SOURCE_DEBUG_EXTENSION = false;

		/** Can the VM request VM death events? */
		public static final boolean CAN_REQUEST_V_M_DEATH_EVENT = false;

		/** Can the VM set a default stratum? */
		public static final boolean CAN_SET_DEFAULT_STRATUM = false;

		/**
		 * Can the VM return instances, counts of instances of classes and
		 * referring objects?
		 */
		public static final boolean CAN_GET_INSTANCE_INFO = false;

		/** Can the VM request monitor events? */
		public static final boolean CAN_REQUEST_MONITOR_EVENTS = false;

		/** Can the VM get monitors with frame depth info? */
		public static final boolean CAN_GET_MONITOR_FRAME_INFO = false;

		/** Can the VM filter class prepare events by source name? */
		public static final boolean CAN_USE_SOURCE_NAME_FILTERS = false;

		/** Can the VM return the constant pool information? */
		public static final boolean CAN_GET_CONSTANT_POOL = false;

		/** Can the VM force early return from a method? */
		public static final boolean CAN_FORCE_EARLY_RETURN = false;
	}

}
