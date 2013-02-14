package com.sun.java.example1;

public class RemoteModelImpl extends java.rmi.server.UnicastRemoteObject
		implements RemoteModelRef {
	LocalModel lm;

	public RemoteModelImpl(LocalModel lm) throws java.rmi.RemoteException {
		super();
		this.lm = lm;
	}

	// Delegate to the local
	// model implementation
	public String getVersionNumber() throws java.rmi.RemoteException {
		System.out.println("remote model");
		return lm.getVersionNumber();
	}
}
