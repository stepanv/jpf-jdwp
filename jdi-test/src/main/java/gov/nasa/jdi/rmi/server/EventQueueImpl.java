package gov.nasa.jdi.rmi.server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;

public class EventQueueImpl extends UnicastRemoteObject implements EventQueue, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2504879355352899222L;

	protected EventQueueImpl() throws RemoteException {
		super();
	}

	@Override
	public VirtualMachine virtualMachine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventSet remove() throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventSet remove(long paramLong) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

}
