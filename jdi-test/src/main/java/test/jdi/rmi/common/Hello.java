package test.jdi.rmi.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

import test.jdi.rmi.client.LocalObject;

public interface Hello extends Remote {
    String sayHello() throws RemoteException;
    
    LocalObject receiveLocalObject() throws RemoteException;
    RemoteObject receiveRemoteObject() throws RemoteException;
    	
}
