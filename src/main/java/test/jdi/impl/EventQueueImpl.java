package test.jdi.impl;

import org.apache.log4j.Logger;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;

/**
 * Needs sychronized methods, otherwise we're in risk, that we add an event into already removed queue..
 * 
 * @author stepan
 *
 */
public class EventQueueImpl implements EventQueue {

	public static final Logger log = org.apache.log4j.Logger.getLogger(EventQueueImpl.class);
	
	private VirtualMachineImpl vm;

	EventSet currentEvents = new EventSetImpl(vm);

	EventQueueImpl(VirtualMachineImpl vm) {
		this.vm = vm;
	}

	@Override
	public VirtualMachine virtualMachine() {
		return vm;
	}

	@Override
	public synchronized EventSet remove() throws InterruptedException {

		EventSet removedEventSet = currentEvents;
		currentEvents = new EventSetImpl(vm);
		
		log.debug("returning event set: " + removedEventSet);

		return removedEventSet;
	}

	@Override
	public synchronized EventSet remove(long paramLong) throws InterruptedException {
		log.debug("removing events");
		Thread.sleep(paramLong);
		return remove();
	}


	public synchronized void addEvent(Event event) {
		log.debug("Adding event: " + event + " to set: " + currentEvents);
		currentEvents.add(event);
	}

}
