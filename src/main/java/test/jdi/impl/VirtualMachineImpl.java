package test.jdi.impl;

import gov.nasa.jdi.rmi.server.InvocationException;
import gov.nasa.jdi.rmi.server.JPFInspectorLauncher;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.inspector.client.JPFInspectorClientInterface;
import gov.nasa.jpf.inspector.interfaces.JPFInspectorBackEndInterface;
import gov.nasa.jpf.inspector.interfaces.JPFInspectorException;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.StaticElementInfo;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.ThreadList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import sun.tools.tree.ThisExpression;
import test.jdi.impl.internal.JPFManager;

import com.sun.jdi.BooleanValue;
import com.sun.jdi.ByteValue;
import com.sun.jdi.CharValue;
import com.sun.jdi.DoubleValue;
import com.sun.jdi.FloatValue;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.LongValue;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ShortValue;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VoidValue;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.request.EventRequestManager;

public class VirtualMachineImpl extends VirtualMachineBaseImpl {

	public static final Logger log = org.apache.log4j.Logger
			.getLogger(VirtualMachineImpl.class);

	private JVM jvm;

	JPFRunner jpfRunner;
	JPFManager jpfManager;

	private JPF jpf;

	private JPFInspectorLauncher inspectorLauncher;

	public JVM getJvm() {
		return jvm;
	}

	public void setJvm(JVM jvm) {
		this.jvm = jvm;
	}

	public VirtualMachineImpl(JPFInspectorLauncher inspectorLauncher)
			throws InvocationException {
		super(inspectorLauncher);

		jpfManager = new JPFManager(this);

		jpf = inspectorLauncher.launch(this);

		jpfRunner = new JPFRunner(jpf);
		setJvm(jpf.getVM());

		getJvm().addListener(new JDIListener(this));

		this.inspectorLauncher = inspectorLauncher;

		inspectorLauncher
				.executeCommand("cr bp state=en pos=oldclassic.java:127");
		inspectorLauncher.executeCommand("show bp");

		log.debug("Breakpoint is set");

	}

	// List<ThreadReferenceImpl> threads = new ArrayList<ThreadReferenceImpl>();

	public VirtualMachineImpl(JPF jpf) {
		super(jpf);

		this.jpf = jpf;
		jpfManager = new JPFManager(this);
		jpfRunner = new JPFRunner(jpf);
		setJvm(jpf.getVM());

		getJvm().addListener(new JDIListener(this));
	}

	LinkedHashMap<ThreadInfo, ThreadReferenceImpl> threads = new LinkedHashMap<ThreadInfo, ThreadReferenceImpl>();

	private boolean suspendOnStart = true;

	@Override
	public List<ThreadReference> allThreads() {
		log.debug("Entering method 'allThreads'");

		LinkedHashMap<ThreadInfo, ThreadReferenceImpl> currentThreads = new LinkedHashMap<ThreadInfo, ThreadReferenceImpl>();

		ThreadList tl = getJvm().getThreadList();
		for (ThreadInfo ti : tl.getThreads()) {
			if (threads.containsKey(ti)) {
				currentThreads.put(ti, threads.get(ti));
			} else {
				ThreadReferenceImpl threadReference = new ThreadReferenceImpl(
						this, ti);
				currentThreads.put(ti, threadReference);
			}
		}
		threads = currentThreads;

		List<ThreadReference> threadsToReturn = new ArrayList<ThreadReference>();
		for (ThreadReferenceImpl tr : threads.values()) {
			threadsToReturn.add(tr);
		}

		return null;
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
		log.debug("Entering method 'allClasses'");
		List<ReferenceType> classes = new ArrayList<ReferenceType>();

		for (Iterator<StaticElementInfo> it = getJvm().getKernelState()
				.getStaticArea().iterator(); it.hasNext();) {
			StaticElementInfo elInfo = it.next();
			classes.add(new ReferenceTypeImpl(elInfo, this));
		}
		return classes;
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

		log.debug("Entering method 'classesByName'");
		classes.add(new ReferenceTypeImpl(ClassInfo
				.getResolvedClassInfo(paramString), this));
		// TODO Auto-generated method stub
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

		public JPFRunner(JPF jpf) {
			this.jpf = jpf;
		}

		@Override
		public void run() {
			jpf.run();

		}

		public void exit() {
			jpf.exit();
			thread.stop();
		}

		private Thread thread;

		public Thread start() {
			thread = new Thread(this);
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
		
			if (suspendOnStart) {
				jpfManager.suspendAllThreads();
			}
		}
	}

	@Override
	public String name() {
		return "Java Path Finder";
	}

	public void addEvent(Event event) {
		eventQueue.addEvent(event);
	}

	public void debugTmp() {
		inspectorLauncher.executeCommand("del bp 1");

		inspectorLauncher.executeCommand("print #thread[1]");
		inspectorLauncher.executeCommand("print #thread[2]");
	}

	public JPFManager getJPFManager() {
		return jpfManager;
	}

}
