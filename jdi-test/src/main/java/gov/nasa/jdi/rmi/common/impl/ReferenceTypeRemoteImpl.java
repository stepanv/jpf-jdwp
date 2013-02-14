package gov.nasa.jdi.rmi.common.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.sun.jdi.ReferenceType;

import gov.nasa.jdi.rmi.common.ReferenceTypeRemote;

public class ReferenceTypeRemoteImpl extends UnicastRemoteObject implements ReferenceTypeRemote {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5779375253472371093L;
	private ReferenceType rt;

	protected ReferenceTypeRemoteImpl(ReferenceType rt) throws RemoteException {
		super();
		this.rt = rt;
	}

	@Override
	public ReferenceType instance() {
		return rt;
	}

}
