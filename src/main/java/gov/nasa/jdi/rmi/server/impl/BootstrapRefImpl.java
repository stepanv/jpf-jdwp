package gov.nasa.jdi.rmi.server.impl;

import java.rmi.RemoteException;

import com.sun.jdi.VirtualMachineManager;

import gov.nasa.jdi.rmi.client.impl.VirtualMachineManagerImpl;
import gov.nasa.jdi.rmi.common.BootstrapRef;

public class BootstrapRefImpl implements BootstrapRef {

	@Override
	public VirtualMachineManager receiveVirtualMachineManager()
			throws RemoteException {
		return VirtualMachineManagerImpl.virtualMachineManager();
	}

}
