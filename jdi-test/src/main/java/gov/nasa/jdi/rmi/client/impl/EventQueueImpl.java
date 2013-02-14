package gov.nasa.jdi.rmi.client.impl;

import java.rmi.RemoteException;

import gov.nasa.jdi.rmi.common.EventQueueRemote;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;

public class EventQueueImpl implements EventQueue {

	private EventQueueRemote eventQueue;

	public EventQueueImpl(EventQueueRemote eventQueue) {
		this.eventQueue = eventQueue;
	}

	@Override
	public VirtualMachine virtualMachine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventSet remove() throws InterruptedException {
		try {
			return new EventSetImpl(eventQueue.remove());
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public EventSet remove(long paramLong) throws InterruptedException {
		try {
			return new EventSetImpl(eventQueue.remove(paramLong));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

}
