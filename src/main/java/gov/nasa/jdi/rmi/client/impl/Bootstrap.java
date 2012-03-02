package gov.nasa.jdi.rmi.client.impl;

import com.sun.jdi.VirtualMachineManager;

public class Bootstrap {
	
	
	public static synchronized VirtualMachineManager virtualMachineManager() {
			return VirtualMachineManagerImpl.virtualMachineManager();
		
	}
}
