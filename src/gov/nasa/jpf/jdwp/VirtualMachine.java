package gov.nasa.jpf.jdwp;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import gnu.classpath.jdwp.Jdwp;
import gnu.classpath.jdwp.event.EventRequest;
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
			Jdwp.notify(vmInitEvent);
			System.out.println("suspending after start");
			suspendAllThreads();
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

	public void resumeAllThreads() {
		synchronized (this) {
			this.notify();
		}
	}

	public void suspendAllThreads() {
		synchronized (this) {
			try {
				allThreadsSuspended = true;
				wait();
			} catch (InterruptedException e) {
			} finally {
				allThreadsSuspended = false;
			}
		}
	}

	public void suspendIfSuspended() {
	}

	public void registerEvent(EventRequest request) {
		// TODO Auto-generated method stub
		
	}

	public JPF getJpf() {
		return jpf;
	}

}
