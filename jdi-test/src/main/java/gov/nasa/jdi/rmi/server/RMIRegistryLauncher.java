package gov.nasa.jdi.rmi.server;

import java.io.IOException;

public class RMIRegistryLauncher {

	private Process registryProcess;
	/**
	 * The easiest way how to launch rmiregistry on this computer. <br/>
	 * TODO need to add port lookup in case that rmiregistry is already running
	 * etc..
	 * @throws InvocationException if process wasn't possible to start properly
	 */
	public void launch() throws InvocationException {
		ProcessBuilder pb = new ProcessBuilder();
		
		pb.command("rmiregistry.exe");
		try {
			registryProcess = pb.start();
		} catch (IOException e) {
			throw new InvocationException("Cannot start RMI Registry", e);
		}
	}
	public Process getProcess() {
		return registryProcess;
	}

}
