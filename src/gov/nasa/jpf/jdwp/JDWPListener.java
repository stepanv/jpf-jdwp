package gov.nasa.jpf.jdwp;

import gnu.classpath.jdwp.Jdwp;
import gnu.classpath.jdwp.event.ClassPrepareEvent;
import gnu.classpath.jdwp.event.Event;
import gnu.classpath.jdwp.event.EventManager;
import gnu.classpath.jdwp.event.EventRequest;
import gnu.classpath.jdwp.event.MethodEntryEvent;
import gnu.classpath.jdwp.event.ThreadStartEvent;
import gnu.classpath.jdwp.event.VmInitEvent;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jdwp.proxy.LocationProxy;
import gov.nasa.jpf.jdwp.proxy.ThreadProxy;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.VMListener;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class JDWPListener extends ListenerAdapter implements VMListener {

	private boolean started;
	private VirtualMachine virtualMachine;
	
	public JDWPListener(JPF jpf, VirtualMachine virtualMachine) {
		this.virtualMachine = virtualMachine;
	}
	
	@Override
	public void methodEntered (JVM vm) {
		virtualMachine.started(vm);
		MethodEntryEvent methodEntryEvent = new MethodEntryEvent(new ThreadProxy(vm.getLastThreadInfo()), new LocationProxy(vm.getLastMethodInfo(), 0 ), vm.getLastMethodInfo().getClassInfo());
		dispatchEvent(methodEntryEvent);
	}
	
	

	@Override
	public void threadStarted(JVM vm) {
		ThreadStartEvent threadStartEvent = new ThreadStartEvent(new ThreadProxy(vm.getLastThreadInfo()));
		
		dispatchEvent(threadStartEvent);
	}
	
	@Override
	public void threadTerminated(JVM vm) {
		// TODO [for PJA] there is not relation 1:1 between thread start and thread death events. (e.g. one thread can die multiple times) and JDI doesn't know what to do about that.
		//if (vmJdi.getEventRequestManager().threadDeathRequests().size() > 0) {
			//ThreadDeathEvent td = new ThreadDeathEventImpl(vmJdi, vm.getLastThreadInfo(), vmJdi.getEventRequestManager().threadDeathRequests().get(0));
			//vmJdi.addEvent(td);
		//}
	}
	
	@Override
	public void classLoaded(JVM vm) {
		virtualMachine.notifyClassLoaded(vm.getLastClassInfo());
		ClassPrepareEvent classPrepareEvent = new ClassPrepareEvent(new ThreadProxy(vm.getCurrentThread()), vm.getLastClassInfo(), 0);
		dispatchEvent(classPrepareEvent);
	}
	
	@Override
	public void executeInstruction(JVM vm) {
		virtualMachine.started(vm);
	}
	
	private void dispatchEvent(Event event) {
		System.out.println("Dispatching event: " + event);
		for (EventRequest request : requests) {
			if (request.matches(event)) {
				try {
					Jdwp.sendEvent(request, event);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static List<EventRequest> requests = new CopyOnWriteArrayList<EventRequest>();
	
	public static void registerEvent(EventRequest request) {
		System.out.println("Request received: " + request);
		requests.add(request);
		
	}
}
