package gov.nasa.jdi.rmi.client.impl;

import gov.nasa.jdi.rmi.common.VirtualMachineRemote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.Transport;
import com.sun.jdi.connect.VMStartException;

//s/ \([a-zA-Z0-9]*\)\(([^)][^)]*) {\)/ \1\2\r\t\t\tlog.debug("Entering method '\1'");/

public class LaunchingConnectorImpl implements LaunchingConnector {
	
	public static final Logger log = org.apache.log4j.Logger.getLogger(LaunchingConnectorImpl.class);

	public LaunchingConnectorImpl() {
		arguments.put("main", new ArgumentImpl("main"));
		arguments.put("options", new ArgumentImpl("options"));
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Transport transport() {
		// TODO Auto-generated method stub
		return null;
	}

	Map<String, Argument> arguments = new HashMap<String, Connector.Argument>();

	@Override
	public Map<String, Argument> defaultArguments() {
		return arguments;
	}

	@Override
	public VirtualMachine launch(Map<String, ? extends Argument> paramMap)
			throws IOException, IllegalConnectorArgumentsException,
			VMStartException {
		
		ProcessBuilder pb = new ProcessBuilder("c:/Programs/Java/jdk1.7.0_04/bin/java.exe", "-Djava.rmi.server.codebase=file:/c:/Users/stepan/Data/workspaces/mthesis/jdi-test/target/classes/", "-classpath", System.getProperty("java.class.path"), "gov.nasa.jdi.rmi.server.Agent");
		Process process = pb.start();
		
		Thread processStdoutGobbler = new Thread(new StreamGobbler(process
				.getInputStream()));
		processStdoutGobbler.start();
		Thread processErrorGobbler = new Thread(new StreamGobbler(process
				.getErrorStream()));
		processErrorGobbler.start();
		
		// TODO check for startup of Agent
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// obtain first object through RMI
		Registry registry = LocateRegistry.getRegistry(null);
		VirtualMachineRemote vmRemote;
		try {
			vmRemote = (VirtualMachineRemote) registry.lookup("VirtualMachineRemote");
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			throw new VMStartException(e.toString(), process);
		}

		// remote virtual machine object
		
		return new VitualMachineImpl(vmRemote);
	}
	
	private static class StreamGobbler implements Runnable {

		private BufferedReader br;

		public StreamGobbler(InputStream is) {
			br = new BufferedReader(new InputStreamReader(is));
		}

		@Override
		public void run() {
			String line;
			try {
				while ((line = br.readLine()) != null) {
					System.out.println("Gobblered line: " + line);
				}
			} catch (IOException e) {
				System.err.println("Gobbler ended");
			}

		}

	}
	

}
