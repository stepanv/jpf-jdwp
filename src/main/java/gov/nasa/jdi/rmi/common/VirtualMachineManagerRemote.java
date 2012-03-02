package gov.nasa.jdi.rmi.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.sun.jdi.connect.LaunchingConnector;

public interface VirtualMachineManagerRemote extends Remote {

	LaunchingConnector defaultConnector() throws RemoteException;

}
