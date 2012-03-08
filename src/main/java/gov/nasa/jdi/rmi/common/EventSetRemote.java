package gov.nasa.jdi.rmi.common;

import gov.nasa.jdi.rmi.common.impl.local.EventLocal;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface EventSetRemote extends Remote {

	int size() throws RemoteException;


	Set<EventLocal> getLocalEvents() throws RemoteException;

}
