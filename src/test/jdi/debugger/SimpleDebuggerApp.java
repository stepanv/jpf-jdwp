package test.jdi.debugger;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachineManager;

public class SimpleDebuggerApp {

	VirtualMachineManager vmm;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main program = new Main();
		
		program.init();
	}
	
	private void init() {
		vmm = Bootstrap.virtualMachineManager();
		
		vmm.
	}

}
