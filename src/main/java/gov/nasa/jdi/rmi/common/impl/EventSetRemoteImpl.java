package gov.nasa.jdi.rmi.common.impl;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;

import gov.nasa.jdi.rmi.common.EventSetRemote;
import gov.nasa.jdi.rmi.common.impl.local.EventLocal;

public class EventSetRemoteImpl implements EventSetRemote {

	Set<EventLocal> eventLocalSet = new HashSet<EventLocal>();
	
	public EventSetRemoteImpl(EventSet eventSet) {
		for (Event event : eventSet) {
			eventLocalSet.add(new EventLocal(event));
		}
	}
	@Override
	public int size() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<EventLocal> getLocalEvents() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
