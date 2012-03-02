package com.sun.java.example1;

import java.net.MalformedURLException;
import java.rmi.Naming;


public class RemoteModelMgrImpl extends java.rmi.server.UnicastRemoteObject
		implements RemoteModelMgr {
	LocalModel lm;
	RemoteModelImpl rmImpl;

	public RemoteModelMgrImpl() throws java.rmi.RemoteException {
		super();
		try {
			Naming.rebind("foo", this);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public RemoteModelRef getRemoteModelRef() throws java.rmi.RemoteException {
		// Lazy instantiation of delgatee
		if (null == lm) {
			lm = new LocalModel();
		}

		// Lazy instantiation of
		// Remote Interface Wrapper
		if (null == rmImpl) {
			rmImpl = new RemoteModelImpl(lm);
		}

		return ((RemoteModelRef) rmImpl);
	}

	public LocalModel getLocalModel() throws java.rmi.RemoteException {
		// Return a reference to the
		// same LocalModel
		// that exists as the delagetee
		// of the RMI remote
		// object wrapper

		// Lazy instantiation of delgatee
		if (null == lm) {
			lm = new LocalModel();
		}

		return lm;
	}
}