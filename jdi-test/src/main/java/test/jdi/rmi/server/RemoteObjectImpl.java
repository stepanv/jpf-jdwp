package test.jdi.rmi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import test.jdi.rmi.common.RemoteObject;

public class RemoteObjectImpl extends UnicastRemoteObject implements RemoteObject {

	protected RemoteObjectImpl() throws RemoteException {
		super();
	}
	
	private String msg = "hello from remote";

	@Override
	public String sayHello() throws RemoteException {
		System.out.println("in server");
		return msg ;
	}

	public void setHello(String hello) {
		msg = hello;
	}

}
