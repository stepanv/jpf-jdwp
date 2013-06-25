package gov.nasa.jpf.jdwp;

import gnu.classpath.jdwp.Jdwp;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jdwp.event.ClassPrepareEvent;
import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.EventRequest;
import gov.nasa.jpf.jdwp.event.ThreadStartEvent;
import gov.nasa.jpf.jdwp.event.VmStartEvent;
import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.VM;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * <h3>Synchronization</h3>
 * It's actually not clear how events and related consecutive debugger actions
 * are synchronized.<br/>
 * At first, a following problem needs to be solved:<br/>
 * <ol>
 * <li>JDWP agent sends an event (a conditional breakpoint for instance (note
 * that the condition is always evaluated at the debugger side, thus a big
 * number of unused breakpoint events is sent))</li>
 * <li>The breakpoint has suspend thread related action</li>
 * <li>The debugger decides that breakpoint condition is not satisfied and sends
 * back resume</li>
 * <li>The JDWP receive resume command.</li>
 * <li>And now the suspend command (related to the breakpoint event) is
 * performed.</li>
 * <li>Deadlock</li>
 * </ol>
 * <h4>Solution</h4>
 * Event send and the related suspend action has to be encapsulated as a atomic
 * action.
 * 
 * </p>
 * 
 * @author stepan
 * 
 */
public class VirtualMachine {
	private JPF jpf;
	private List<ClassInfo> loadedClases = new CopyOnWriteArrayList<ClassInfo>();
	private boolean started;

	static final Logger logger = LoggerFactory.getLogger(VirtualMachine.class);

	public boolean isStarted() {
		return started;
	}

	public VirtualMachine(JPF jpf) {
		this.jpf = jpf;
	}

	public void started(VM vm, List<ClassInfo> postponedLoadedClasses) {
		if (!started) {
			started = true;
			logger.info("About to send vm started event .. sending postponed class loads.");
			
			synchronized (this) {
				VmStartEvent vmInitEvent = new VmStartEvent(vm.getCurrentThread());
				logger.info("Notifying about vm started");
				Jdwp.notify(vmInitEvent);
				logger.debug("Not suspending after start");
			}
			
			synchronized (this) {
				// TODO according to JDWP specs classprepare events can be in a
				// composite event only if are for the same class
				for (ClassInfo classInfo : postponedLoadedClasses) {
					Event event = new ClassPrepareEvent(vm.getCurrentThread(), classInfo, 0);
					Jdwp.notify(event);
				}
				postponedLoadedClasses.clear();
			}
			// suspendAllThreads();

			// we also need to send thread start event
			// TODO [for PJA] is this a bug in JPF main thread start doesn't
			// trigger threadStarted event in JPF listeners
			synchronized (this) {
				Jdwp.notify(new ThreadStartEvent(vm.getCurrentThread()));
			}
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
	private List<EventRequest<?>> requests = new CopyOnWriteArrayList<EventRequest<?>>();

	public synchronized void resumeAllThreads() {
		logger.debug("Resuming all threads by: {}", Thread.currentThread());
		allThreadsSuspended = false;
		this.notify();
	}

	public synchronized void suspendAllThreads() {
		allThreadsSuspended = true;
	}

	public synchronized void suspendAllThreadsAndSuspend() {
		// TODO throw an exception if error occured
		try {
			allThreadsSuspended = true;
			logger.debug("Suspending all threads in: {}", Thread.currentThread());
			wait();
		} catch (InterruptedException e) {
		} finally {
			allThreadsSuspended = false;
			logger.debug("All threads resumed in: {}", Thread.currentThread());
		}
	}

	public synchronized void suspendIfSuspended() {
		while (allThreadsSuspended) {
			try {
				logger.debug("Suspending all threads in: {}", Thread.currentThread());
				wait();
			} catch (InterruptedException e) {
			}
		}
	}

	public List<EventRequest<?>> getRequests() {
		return requests;
	}

	public void registerEventRequest(EventRequest<?> eventRequest) {
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
		public static final boolean CAN_GET_BYTECODES = true;

		/**
		 * Can the VM determine whether a field or method is synthetic? (that
		 * is, can the VM determine if the method or the field was invented by
		 * the compiler?)
		 */
		public static final boolean CAN_GET_SYNTHETIC_ATTRIBUTE = true;

		/** Can the VM get the owned monitors information for a thread? */
		public static final boolean CAN_GET_OWNED_MONITOR_INFO = true;

		/** Can the VM get the current contended monitor of a thread? */
		public static final boolean CAN_GET_CURRENT_CONTENDED_MONITOR = true;

		/** Can the VM get the monitor information for a given object? */
		public static final boolean CAN_GET_MONITOR_INFO = true;
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

		/** Can the VM get the owned monitors information for a thread? */
		public static final boolean CAN_GET_OWNED_MONITOR_INFO = Capabilities.CAN_GET_OWNED_MONITOR_INFO;

		/** Can the VM get the current contended monitor of a thread? */
		public static final boolean CAN_GET_CURRENT_CONTENDED_MONITOR = Capabilities.CAN_GET_CURRENT_CONTENDED_MONITOR;

		/** Can the VM get the monitor information for a given object? */
		public static final boolean CAN_GET_MONITOR_INFO = Capabilities.CAN_GET_MONITOR_INFO;

		/** Can the VM redefine classes? */
		public static final boolean CAN_REDEFINE_CLASSES = true;

		/** Can the VM add methods when redefining classes? */
		public static final boolean CAN_ADD_METHOD = true;

		/** Can the VM redefine classes in arbitrary ways? */
		public static final boolean CAN_UNRESTRICTEDLY_REDEFINE_CLASSES = true;

		/** Can the VM pop stack frames? */
		public static final boolean CAN_POP_FRAMES = true;

		/** Can the VM filter events by specific object? */
		public static final boolean CAN_USE_INSTANCE_FILTERS = true;

		/** Can the VM get the source debug extension? */
		public static final boolean CAN_GET_SOURCE_DEBUG_EXTENSION = false;
		// TODO seems there is nothing related in JPF

		/** Can the VM request VM death events? */
		public static final boolean CAN_REQUEST_V_M_DEATH_EVENT = true;

		/** Can the VM set a default stratum? */
		public static final boolean CAN_SET_DEFAULT_STRATUM = true;

		/**
		 * Can the VM return instances, counts of instances of classes and
		 * referring objects?
		 */
		public static final boolean CAN_GET_INSTANCE_INFO = true;

		/** Can the VM request monitor events? */
		public static final boolean CAN_REQUEST_MONITOR_EVENTS = true;

		/** Can the VM get monitors with frame depth info? */
		public static final boolean CAN_GET_MONITOR_FRAME_INFO = true;

		/** Can the VM filter class prepare events by source name? */
		public static final boolean CAN_USE_SOURCE_NAME_FILTERS = true;

		/** Can the VM return the constant pool information? */
		public static final boolean CAN_GET_CONSTANT_POOL = true;

		/** Can the VM force early return from a method? */
		public static final boolean CAN_FORCE_EARLY_RETURN = true;
	}

	private List<ObjectId> disableCollectionObjects = new CopyOnWriteArrayList<ObjectId>();

	public void disableCollection(ObjectId objectId) throws InvalidObject {
		synchronized (disableCollectionObjects) {
			// TODO maybe we should use counters
			objectId.disableCollection();
			if (!disableCollectionObjects.contains(objectId)) {
				disableCollectionObjects.add(objectId);
			}
		}

	}

	public void enableCollection(ObjectId objectId) throws InvalidObject {
		synchronized (disableCollectionObjects) {
			// TODO maybe we should use counters
			objectId.enableCollection();
			if (disableCollectionObjects.contains(objectId)) {
				disableCollectionObjects.remove(objectId);
			}
		}
	}

	public void collectAllDisabledObjects() {
		synchronized (disableCollectionObjects) {
			for (Iterator<ObjectId> iteratorObjectId = disableCollectionObjects.iterator(); iteratorObjectId.hasNext();) {
				ObjectId objectId = iteratorObjectId.next();

				try {
					objectId.enableCollection();
				} catch (InvalidObject e) {
				}
				disableCollectionObjects.remove(objectId);
			}
		}
	}

	private int exitCode = 0;
	private boolean inExit = false;
	
	/**
	 * TODO finish this (it's not used)
	 */
	public void exit(int exitCode) {
		this.exitCode = exitCode;
		this.inExit = true;
		
		// notify the JPF thread if it is suspended
		resumeAllThreads();
	}

	/**
	 * TODO finish this
	 */
	public void exitIfInExit() {
		if (inExit) {
			JPF.exit();
		}
	}

}