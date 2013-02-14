package gov.nasa.jdi.rmi.client.impl;

import gov.nasa.jdi.rmi.common.EventSetRemote;
import gov.nasa.jdi.rmi.common.impl.local.EventLocal;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventSet;
/*
%s/\(@Override\n[\t]*public .* \)\([a-zA-Z0-9]*\)\(([^)]*)\)\( {\)/\1 \2\3\4\r\t\t\/* TODO not implemented logging *\/ log.debug("Entering method '\2\3'");/
*/
public class EventSetImpl implements EventSet {
	public static final Logger log = org.apache.log4j.Logger.getLogger(EventSetImpl.class);

	private EventSetRemote eventSetRemote;
	
	Set<Event> localEventSet;

	public EventSetImpl(EventSetRemote eventSetRemote) {
		this.eventSetRemote = eventSetRemote;
		
		this.localEventSet = new HashSet<Event>();
		try {
			for (EventLocal eventLocal : eventSetRemote.getLocalEvents()) {
				localEventSet.add(eventLocal);
			}
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public VirtualMachine  virtualMachine() {
		/* TODO not implemented logging */ log.debug("Entering method 'virtualMachine()'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int  size() {
		/* TODO not implemented logging */ log.debug("Entering method 'size()'");
		try {
			return eventSetRemote.size();
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean  isEmpty() {
		/* TODO not implemented logging */ log.debug("Entering method 'isEmpty()'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean  contains(Object o) {
		/* TODO not implemented logging */ log.debug("Entering method 'contains(Object o)'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<Event>  iterator() {
		/* TODO not implemented logging */ log.debug("Entering method 'iterator()'");
		
		return null;
	}

	@Override
	public Object[]  toArray() {
		/* TODO not implemented logging */ log.debug("Entering method 'toArray()'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[]  toArray(T[] a) {
		/* TODO not implemented logging */ log.debug("Entering method 'toArray(T[] a)'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean  add(Event e) {
		/* TODO not implemented logging */ log.debug("Entering method 'add(Event e)'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean  remove(Object o) {
		/* TODO not implemented logging */ log.debug("Entering method 'remove(Object o)'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean  containsAll(Collection<?> c) {
		/* TODO not implemented logging */ log.debug("Entering method 'containsAll(Collection<?> c)'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean  addAll(Collection<? extends Event> c) {
		/* TODO not implemented logging */ log.debug("Entering method 'addAll(Collection<? extends Event> c)'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean  retainAll(Collection<?> c) {
		/* TODO not implemented logging */ log.debug("Entering method 'retainAll(Collection<?> c)'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean  removeAll(Collection<?> c) {
		/* TODO not implemented logging */ log.debug("Entering method 'removeAll(Collection<?> c)'");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void  clear() {
		/* TODO not implemented logging */ log.debug("Entering method 'clear()'");
		// TODO Auto-generated method stub

	}

	@Override
	public int  suspendPolicy() {
		/* TODO not implemented logging */ log.debug("Entering method 'suspendPolicy()'");
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public EventIterator  eventIterator() {
		/* TODO not implemented logging */ log.debug("Entering method 'eventIterator()'");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void  resume() {
		/* TODO not implemented logging */ log.debug("Entering method 'resume()'");
		// TODO Auto-generated method stub

	}

}
