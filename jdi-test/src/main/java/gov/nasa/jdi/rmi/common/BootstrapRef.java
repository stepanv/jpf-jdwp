package gov.nasa.jdi.rmi.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.sun.jdi.VirtualMachineManager;

public interface BootstrapRef extends Remote {
	VirtualMachineManager receiveVirtualMachineManager() throws RemoteException;
}
