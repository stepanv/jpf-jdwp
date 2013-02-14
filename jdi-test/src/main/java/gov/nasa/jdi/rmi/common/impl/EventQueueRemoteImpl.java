package gov.nasa.jdi.rmi.common.impl;

import gov.nasa.jdi.rmi.common.EventQueueRemote;
import gov.nasa.jdi.rmi.common.EventSetRemote;
import gov.nasa.jdi.rmi.common.VirtualMachineRemote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.sun.jdi.event.EventQueue;

public class EventQueueRemoteImpl extends UnicastRemoteObject implements EventQueueRemote {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1034289165374363042L;


	public EventQueueRemoteImpl(EventQueue eventQueue) throws RemoteException {
		super();
		this.eventQueue = eventQueue;
	}

	private EventQueue eventQueue;


	@Override
	public VirtualMachineRemote virtualMachine() throws RemoteException {
		return new VirtualMachineRemoteImpl(eventQueue.virtualMachine());
	}

	@Override
	public EventSetRemote remove() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventSetRemote remove(long paramLong) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventQueue instance() {
		return eventQueue;
	}

}
