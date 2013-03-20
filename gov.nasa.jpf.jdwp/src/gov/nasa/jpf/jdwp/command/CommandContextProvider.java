package gov.nasa.jpf.jdwp.command;


import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jdwp.JdwpObjectManager;
import gov.nasa.jpf.jdwp.VirtualMachine;
import gov.nasa.jpf.jvm.JVM;

public class CommandContextProvider {
	
	private VirtualMachine virtualMachine;
	private JdwpObjectManager objectManager;

	public CommandContextProvider(VirtualMachine virtualMachine, JdwpObjectManager objectManager) {
		this.virtualMachine = virtualMachine;
		this.objectManager = objectManager;
	}
	public JdwpObjectManager getObjectManager() {
		return objectManager;
	}
	
	public JVM getJVM() {
		return JVM.getVM();
	}
	
	public JPF getJPF() {
		return getJVM().getJPF();
	}
	
	public VirtualMachine getVirtualMachine() {
		return virtualMachine;
	}
	

}
