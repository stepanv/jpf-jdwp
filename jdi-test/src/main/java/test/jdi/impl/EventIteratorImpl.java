package test.jdi.impl;

import java.util.Iterator;

import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;

public class EventIteratorImpl implements EventIterator {


	private Iterator<Event> eventIterator;

	public EventIteratorImpl(Iterator<Event> eventIterator) {
		this.eventIterator = eventIterator;
	}

	@Override
	public boolean hasNext() {
		return eventIterator.hasNext();
	}

	@Override
	public Event next() {
		return eventIterator.next();
	}

	@Override
	public void remove() {
		eventIterator.remove();

	}

	@Override
	public Event nextEvent() {
		return eventIterator.next();
	}

}
