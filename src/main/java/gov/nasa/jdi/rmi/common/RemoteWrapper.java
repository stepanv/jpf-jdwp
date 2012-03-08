package gov.nasa.jdi.rmi.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteWrapper<E> extends Remote {
	public E instance() throws RemoteException;
}
