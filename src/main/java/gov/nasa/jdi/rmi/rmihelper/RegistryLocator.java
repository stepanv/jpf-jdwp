package gov.nasa.jdi.rmi.rmihelper;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RegistryLocator {
private static Registry registry;
	
	static {
		try {
			registry = LocateRegistry.getRegistry(null);
			
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Object lookup(String className) {
		try {
			return registry.lookup(className);
		} catch (RemoteException|NotBoundException e) {
			throw new RuntimeException("Cannot lookup " + className, e);
		}
	}
}
