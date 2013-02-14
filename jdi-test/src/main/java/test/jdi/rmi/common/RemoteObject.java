package test.jdi.rmi.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteObject extends Remote {
	public String sayHello() throws RemoteException;
	
}
