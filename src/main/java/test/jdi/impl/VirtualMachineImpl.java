package test.jdi.impl;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPF.ExitException;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.StaticElementInfo;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.ThreadList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;

import test.jdi.impl.internal.ClassesManager;
import test.jdi.impl.internal.JDIListener;
import test.jdi.impl.internal.JPFManager;
import test.jdi.impl.internal.ThreadManager;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.VMDeathRequest;

public class VirtualMachineImpl extends VirtualMachineBaseImpl {

	public static final Logger log = org.apache.log4j.Logger
			.getLogger(VirtualMachineImpl.class);

	private JVM jvm;

	JPFRunner jpfRunner;
	JPFManager jpfManager;
	ThreadManager threadManager;

	public ThreadManager getThreadManager() {
		return threadManager;
	}

	private JPF jpf;

	public JVM getJvm() {
		return jvm;
	}

	public void setJvm(JVM jvm) {
		this.jvm = jvm;
	}

	public VirtualMachineImpl(JPF jpf) {
		super(jpf);

		this.jpf = jpf;
		jpfManager = new JPFManager(this);
		threadManager = new ThreadManager(this);
		jpfRunner = new JPFRunner(jpf, this);
		classesManager = new ClassesManager(this);
		jvm = jpf.getVM();

		getJvm().addListener(new JDIListener(this));
	}

	LinkedHashMap<ThreadInfo, ThreadReferenceImpl> threads = new LinkedHashMap<ThreadInfo, ThreadReferenceImpl>();

	private boolean suspendOnStart = true;

	@Override
	public List<ThreadReference> allThreads() {
		log.debug("Entering method 'allThreads'");

		updateThreads();

		List<ThreadReference> threadsToReturn = new ArrayList<ThreadReference>();
		for (ThreadReferenceImpl tr : threads.values()) {
			threadsToReturn.add(tr);
		}

		return threadsToReturn;
	}

	private void updateThreads() {
		LinkedHashMap<ThreadInfo, ThreadReferenceImpl> currentThreads = new LinkedHashMap<ThreadInfo, ThreadReferenceImpl>();

		ThreadList tl = getJvm().getThreadList();
		for (ThreadInfo ti : tl.getThreads()) {
			if (threads.containsKey(ti)) {
				currentThreads.put(ti, threads.get(ti));
			} else {
				ThreadReferenceImpl threadReference = ThreadReferenceImpl.factory(ti, this);
				currentThreads.put(ti, threadReference);
			}
		}
		threads = currentThreads;
	}
	
	public LinkedHashMap<ThreadInfo, ThreadReferenceImpl> getThreads() {
		updateThreads();
		return threads;
	}

	public void start() {
		jpfRunner.start();
	}

	@Override
	public void exit(int paramInt) {
		log.debug("Entering method 'exit'");
		jpfRunner.exit();
	}

	@Override
	public List<ReferenceType> allClasses() {
		return null;
	}

	@Override
	public VirtualMachine virtualMachine() {
		log.debug("Entering method 'virtualMachine'");
		return this;
	}

	@Override
	public List<ReferenceType> classesByName(String paramString) {
		List<ReferenceType> classes = new ArrayList<ReferenceType>();

		// for (Iterator<StaticElementInfo> it =
		// jvm.getKernelState().getStaticArea().iterator(); it.hasNext(); ) {
		// StaticElementInfo elInfo = it.next();
		// classes.add(new ReferenceTypeImpl(elInfo));
		// }
		// classes.get(75);

		log.debug("Entering method 'classesByName' for: " + paramString);
		for (ClassInfo classInfo : classesManager.getLoadedClasses()) {
			if (paramString.equals(classInfo.getName())) {
				classes.add(ClassTypeImpl.factory(classInfo, this));
			}
		}
		return classes;
	}

	@Override
	public void resume() {
		log.debug("Entering method 'resume'");

		synchronized (started) {
			if (!started) {
				suspendOnStart = false;
			} else {
				jpfManager.resumeAllThreads();
			}
		}

	}

	private EventQueueImpl eventQueue = new EventQueueImpl(this);

	private Boolean started = Boolean.FALSE;

	@Override
	public EventQueue eventQueue() {
		// log.debug("Entering method 'eventQueue'");
		return eventQueue;
	}

	EventRequestManagerImpl eventRequestManager = new EventRequestManagerImpl(
			this);

	private ClassesManager classesManager;

	public EventRequestManagerImpl getEventRequestManager() {
		return eventRequestManager;
	}

	@Override
	public EventRequestManager eventRequestManager() {
		log.debug("Entering method 'eventRequestManager'");
		// TODO Auto-generated method stub
		return eventRequestManager;
	}

	private class JPFRunner implements Runnable {

		private JPF jpf;
		private VirtualMachineImpl virtualMachineImpl;

		public JPFRunner(JPF jpf, VirtualMachineImpl virtualMachineImpl) {
			this.jpf = jpf;
			this.virtualMachineImpl = virtualMachineImpl;
		}

		@Override
		public void run() {
			try {
				jpf.run();
			} catch (Throwable t) {
				// Catching all possible exceptions
				t.printStackTrace();
			} finally {
				log.info("JPF stopped");
				VMDeathRequest vmDeathRequest = null;
				if (virtualMachineImpl.eventRequestManager.vmDeathRequests().size() > 0) {
					vmDeathRequest = virtualMachineImpl.eventRequestManager.vmDeathRequests().remove(0);
				}
				VMDeathEvent vmDeath = new VMDeathEventImpl(virtualMachineImpl, vmDeathRequest);
				virtualMachineImpl.addEvent(vmDeath);
			}

		}

		public void exit() {
			try {
			jpf.exit();
			thread.stop();
			} catch (ExitException ee) {
				log.debug("Exit exception caught TODO"); // TODO [for PJA] how to end JPF correctly?
			}
		}

		private Thread thread;

		public Thread start() {
			thread = new Thread(this);
			thread.setName("JPF Runner thread");
			thread.start();
			return thread;
		}

		public void joinHard() {
			while (true) {
				try {
					thread.join();
					return;
				} catch (InterruptedException e) {
				}
			}
		}

	}

	/**
	 * TODO remove this as a temporary workaround This gives us a way how to
	 * disable execution till the client is ready (and calls
	 * {@link VirtualMachine#resume()}
	 */
	public void started() {
		if (started.equals(Boolean.FALSE)) {
			synchronized (started) {
			
				log.debug("adding event VM started");
				eventQueue.addEvent(new VMStartEventImpl());
				log.debug("adding event VM started .. done");
				started = true;
			}
		
			// we also want to send ThreadStarted Event because for the MAIN thread .. JPF listener doesn't work as expected
			// TODO [for PJA] is this really desired behavior of JPF?
			if (getEventRequestManager().threadStartRequests().size() > 0) {
				ThreadStartEvent te = new ThreadStartEventImpl(this, jvm.getCurrentThread(), getEventRequestManager().threadStartRequests().get(0));
				addEvent(te);
			}
		
			if (suspendOnStart) {
				jpfManager.suspendAllThreads();
			}
		}
	}
	
	@Override
	public void suspend() {
		log.debug("Entering method 'suspend'");
		jpfManager.suspendAllThreads();
	}

	@Override
	public String name() {
		return "Java Path Finder";
	}

	public void addEvent(Event event) {
		eventQueue.addEvent(event);
	}

	public void debugTmp() {
		log.debug("NOT REALLY DEBUGGING ANYTHING...");
	}

	public JPFManager getJPFManager() {
		return jpfManager;
	}

	public ClassesManager getClassesManager() {
		return classesManager;
	}

}
