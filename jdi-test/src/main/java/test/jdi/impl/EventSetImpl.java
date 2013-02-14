package test.jdi.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventSet;

public class EventSetImpl implements EventSet {

	private VirtualMachineImpl vmImpl;

	public EventSetImpl(VirtualMachineImpl vmImpl) {
		this.vmImpl = vmImpl;
	}

	Set<Event> eventSetInternal = Collections.newSetFromMap(new ConcurrentHashMap<Event,Boolean>());

	@Override
	public VirtualMachine virtualMachine() {
		return vmImpl;
	}

	@Override
	public int size() {
		return eventSetInternal.size();
	}

	@Override
	public boolean isEmpty() {
		return eventSetInternal.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return eventSetInternal.contains(o);
	}

	@Override
	public Iterator<Event> iterator() {
		return eventSetInternal.iterator();
	}

	@Override
	public Object[] toArray() {
		return eventSetInternal.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return toArray(a);
	}

	@Override
	public boolean add(Event e) {
		return eventSetInternal.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return eventSetInternal.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return eventSetInternal.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Event> c) {
		return eventSetInternal.addAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return eventSetInternal.retainAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return eventSetInternal.removeAll(c);
	}

	@Override
	public void clear() {
		eventSetInternal.clear();
	}

	@Override
	public int suspendPolicy() {
		return 0;
	}

	@Override
	public EventIterator eventIterator() {
		return new EventIteratorImpl(iterator());
	}

	@Override
	public void resume() {
		vmImpl.resume();
	}

}
