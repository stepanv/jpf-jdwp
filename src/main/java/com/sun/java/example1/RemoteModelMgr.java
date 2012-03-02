package com.sun.java.example1;

public interface RemoteModelMgr extends java.rmi.Remote {
	public RemoteModelRef getRemoteModelRef() throws java.rmi.RemoteException;

	public LocalModel getLocalModel() throws java.rmi.RemoteException;
}
