package test.jdi.rmi.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

import com.sun.java.example1.RemoteModelMgr;
import com.sun.jdi.Field;

import test.jdi.rmi.common.Hello;
import test.jdi.rmi.common.RemoteObject;

public class Client {
	
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

	public static void main(String[] args) {

		String host = (args.length < 1) ? null : args[0];
		try {
			

			Hello stub = (Hello) registry.lookup("Hello");
			String response = stub.sayHello();
			System.out.println("response: " + response);
			
			
			LocalObject lo = stub.receiveLocalObject();
			System.out.println(lo.sayHello());
			
			RemoteObject ro = stub.receiveRemoteObject();
			
			
			while (true) {
				System.out.println(ro.sayHello());
			}
			
//			RemoteModelMgr stub2 = (RemoteModelMgr) registry.lookup("foo");
//			
//			System.out.println(stub2.getRemoteModelRef().getVersionNumber());
//			System.out.println(stub2.getLocalModel().getVersionNumber());
			
			
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}
}
