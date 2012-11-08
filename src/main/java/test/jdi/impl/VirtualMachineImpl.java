package test.jdi.impl;

import gov.nasa.jdi.rmi.server.InvocationException;
import gov.nasa.jdi.rmi.server.JPFInspectorLauncher;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.inspector.client.JPFInspectorClientInterface;
import gov.nasa.jpf.inspector.interfaces.JPFInspectorBackEndInterface;
import gov.nasa.jpf.inspector.interfaces.JPFInspectorException;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.StaticElementInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

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

	public static final Logger log = org.apache.log4j.Logger.getLogger(VirtualMachineImpl.class);
	
	private JVM jvm;

	JPFRunner jpfRunner;
	JPFInspectorLauncher inspectorLauncher;
	
	public VirtualMachineImpl(JPFInspectorLauncher inspectorLauncher) throws InvocationException {
		super(inspectorLauncher);
		JPF jpf = inspectorLauncher.launch(this);
		
		jpfRunner = new JPFRunner(jpf);
		jvm = jpf.getVM();
		
		jvm.addListener(new JDIListener(this));
		
		this.inspectorLauncher = inspectorLauncher;
		
		inspectorLauncher.executeCommand("cr bp state=en pos=oldclassic.java:127");
		inspectorLauncher.executeCommand("show bp");
		
		log.debug("Breakpoint is set");
		
	}
	
	public void start() {
		jpfRunner.start();
	}

	@Override
	public List<ReferenceType> allClasses() {
		log.debug("Entering method 'allClasses'");
		List<ReferenceType> classes = new ArrayList<ReferenceType>();
		
		for (Iterator<StaticElementInfo> it = jvm.getKernelState().getStaticArea().iterator(); it.hasNext(); ) {
			StaticElementInfo elInfo = it.next();
			classes.add(new ReferenceTypeImpl(elInfo));
		}
		return classes;
	}

	@Override
	public void resume() {
		log.debug("Entering method 'resume'");
		try {
			inspectorLauncher.getInspector().start();
		} catch (JPFInspectorException e) {
			throw new RuntimeException("Cannot resume", e);
		}
	}

	private EventQueueImpl eventQueue = new EventQueueImpl(this);

	private boolean started;

	@Override
	public EventQueue eventQueue() {
		//log.debug("Entering method 'eventQueue'");
		return eventQueue;
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

	public void started() {
		if (started == false) {
			log.debug("adding event VM started");
			eventQueue.addEvent(new VMStartEventImpl());
			started = true;
			log.debug("adding event VM started .. done");
		}
		
	}
	
	public void addEvent(Event event) {
		eventQueue.addEvent(event);
	}
	
	public void debugTmp() {
		inspectorLauncher.executeCommand("del bp 1");
		
		inspectorLauncher.executeCommand("print #thread[1]");
		inspectorLauncher.executeCommand("print #thread[2]");
	}

}
