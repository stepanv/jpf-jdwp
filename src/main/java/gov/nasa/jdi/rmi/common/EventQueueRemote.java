package gov.nasa.jdi.rmi.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.sun.jdi.event.EventQueue;

public interface EventQueueRemote extends Remote, RemoteWrapper<EventQueue> {
	public abstract EventSetRemote remove() throws RemoteException;

	public abstract EventSetRemote remove(long paramLong) throws RemoteException;

	VirtualMachineRemote virtualMachine() throws RemoteException;
}
