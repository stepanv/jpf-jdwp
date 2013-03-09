package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.Event.EventKind;
import gov.nasa.jpf.jdwp.exception.InvalidCount;
import gov.nasa.jpf.jdwp.exception.JdwpException;

public class CountFilter extends Filter<Event> {

	private int count;
	private boolean expired;

	public CountFilter(int count) throws JdwpException {
		super(Filter.ModKind.COUNT);

		if (count <= 0) {
			throw new InvalidCount();
		}

		this.count = count;
		this.expired = false;
	}

	@Override
	public boolean matches(Event event) {
		assert expired == false;

		if (--count > 0) {
			return false;
		}
		expired = true;
		return --count == 0;
	}

	/**
	 * According to spec, any event is allowed.
	 */
	@Override
	public boolean isAllowedEventKind(EventKind eventKind) {
		return true;
	}

}
