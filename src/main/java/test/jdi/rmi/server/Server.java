package test.jdi.rmi.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

import test.jdi.rmi.client.LocalObject;
import test.jdi.rmi.common.Hello;
import test.jdi.rmi.common.RemoteObject;

import com.sun.java.example1.RemoteModelMgrImpl;
import com.sun.java.example1.RemoteModelMgr;

public class Server implements Hello {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5841984691370000198L;

	public Server() {
		try {
			ro = new RemoteObjectImpl();
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	public String sayHello() {
		return "Hello, world!";
	}
	
	private RemoteObject ro;

	public static void main(String args[]) {

		try {
			Server srv  = new Server();
			Hello stub = (Hello) UnicastRemoteObject.exportObject(srv, 0);
			RemoteModelMgr obj = new RemoteModelMgrImpl();
			
			
			//RemoteModelMgr stub2 = (RemoteModelMgr)  UnicastRemoteObject.exportObject(obj, 0);

			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry();
			registry.bind("Hello", stub);
			
			//registry.bind("RemoteModelMgr", stub2);

			System.err.println("Server ready");
			
			while (true) {
				int rnd = new Random().nextInt();
				System.out.println("generated : " + rnd);
				
				((RemoteObjectImpl)srv.ro).setHello("zmeneno 2 na; " + rnd);
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public LocalObject receiveLocalObject() throws RemoteException {
		return new LocalObject(new LocalObject.Nonserializable("Foo"));
	}

	@Override
	public RemoteObject receiveRemoteObject() throws RemoteException {
		return ro;
	}

}
