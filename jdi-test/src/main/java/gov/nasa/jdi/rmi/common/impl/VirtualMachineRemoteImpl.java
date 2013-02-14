package gov.nasa.jdi.rmi.common.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import test.jdi.impl.ReferenceTypeImpl;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.EventQueue;

import gov.nasa.jdi.rmi.common.EventQueueRemote;
import gov.nasa.jdi.rmi.common.ReferenceTypeRemote;
import gov.nasa.jdi.rmi.common.VirtualMachineRemote;
import gov.nasa.jdi.rmi.server.EventQueueImpl;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.StaticElementInfo;

public class VirtualMachineRemoteImpl extends UnicastRemoteObject implements VirtualMachineRemote {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6950316612092338621L;
	private VirtualMachine vm;
	
	public VirtualMachineRemoteImpl(VirtualMachine vm) throws RemoteException {
		super();
		this.vm = vm;
	}

	@Override
	public List<ReferenceTypeRemote> classesByName(String paramString)
			throws RemoteException {
		
		List<ReferenceTypeRemote> rttList = new ArrayList<ReferenceTypeRemote>();
		for (ReferenceType rt : vm.classesByName(paramString)) {
			rttList.add(new ReferenceTypeRemoteImpl(rt));
		}
		
		return rttList;
	}

	@Override
	public void resume() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EventQueueRemote eventQueue() throws RemoteException {
		return new EventQueueRemoteImpl(vm.eventQueue());
	}

	@Override
	public VirtualMachine instance() {
		return vm;
	}

}
