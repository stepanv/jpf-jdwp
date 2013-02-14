package gov.nasa.jdi.rmi.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import com.sun.jdi.VirtualMachine;

public interface VirtualMachineRemote extends Remote, RemoteWrapper<VirtualMachine> {

	List<ReferenceTypeRemote> classesByName(String paramString) throws RemoteException;

	void resume() throws RemoteException;

	EventQueueRemote eventQueue() throws RemoteException;

}
