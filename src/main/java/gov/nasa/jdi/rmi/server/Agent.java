package gov.nasa.jdi.rmi.server;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import test.jdi.rmi.common.Hello;

import gov.nasa.jdi.rmi.common.VirtualMachineRemote;
import gov.nasa.jdi.rmi.common.impl.VirtualMachineRemoteImpl;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.inspector.interfaces.JPFInspectorException;
import gov.nasa.jpf.shell.ShellManager;

/**
 * Main program of JPF which runs Agent first.
 * Agent could be enriched with standard JDWP implementation.
 * 
 * Right now agent support only RMI communication.
 * 
 * @author stepan
 *
 */
public class Agent {
	
	private RMIRegistryLauncher registryLauncher = new RMIRegistryLauncher();
	private JPFInspectorLauncher inspectorLauncher = new JPFInspectorLauncher();
	
	public JPF jpf;

	private Agent() throws InvocationException {
		registryLauncher.launch();
		
		jpf = inspectorLauncher.launch();
		
		try {
			Registry registry = LocateRegistry.getRegistry();
			VirtualMachineRemote vmr = new VirtualMachineRemoteImpl(new VirtualMachineImpl(jpf.getVM()));
			registry.bind("VirtualMachineRemote", vmr);
		} catch (RemoteException | AlreadyBoundException e) {
			throw new InvocationException(e);
		}
		
		
	}
	
	private class JPFRunner implements Runnable {

		private JPF jpf;
		public JPFRunner(JPF jpf) {
			this.jpf = jpf;
		}
		@Override
		public void run() {
			jpf.run();
			
		}
		
		private Thread thread;
		public Thread start() {
			thread = new Thread(this);
			thread.start();
			return thread;
		}
		public void joinHard() {
			while (true) {
				try {
					thread.join();
					return;
				} catch (InterruptedException e) {
				}
			}
		}
		
	}
	
	private JPFRunner prepare() {
		inspectorLauncher.executeCommand("cr bp state=en pos=oldclassic.java:127");
		inspectorLauncher.executeCommand("show bp");
		
		return new JPFRunner(jpf);
	}
	
	private void debug() {
//		inspectorLauncher.executeCommand("step_over");
//		inspectorLauncher.executeCommand("step_over");
		
		try {
			
			inspectorLauncher.executeCommand("del bp 1");
			
			inspectorLauncher.executeCommand("print #thread[1]");
			inspectorLauncher.executeCommand("print #thread[2]");
					
			inspectorLauncher.getInspector().start();
		} catch (JPFInspectorException e) {
			e.printStackTrace();
		}
	}
	
	private void run() {
		try {
			JPFRunner jpfRunner = agentInstance.prepare();
			jpfRunner.start();
			
			Thread.sleep(10000);
			System.out.println("resuming");
			
			agentInstance.debug();
			
			
			jpfRunner.joinHard();
			registryLauncher.getProcess().destroy();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static Agent agentInstance;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
			try {
				agentInstance = new Agent();
				agentInstance.run();
			} catch (InvocationException e) {
				e.printStackTrace();
			}
	}

}
