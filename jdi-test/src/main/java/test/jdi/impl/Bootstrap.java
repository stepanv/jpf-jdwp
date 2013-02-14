package test.jdi.impl;

import com.sun.jdi.VirtualMachineManager;

public class Bootstrap {

	public static synchronized VirtualMachineManager virtualMachineManager() {
		return VirtualMachineManagerImpl.virtualMachineManager();
	}
}
