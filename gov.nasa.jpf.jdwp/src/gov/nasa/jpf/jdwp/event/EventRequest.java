package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.event.Event.EventKind;
import gov.nasa.jpf.jdwp.event.filter.Filter;
import gov.nasa.jpf.jdwp.exception.JdwpError;

import java.util.ArrayList;
import java.util.List;

public class EventRequest {
	
	private List<Filter> filters = new ArrayList<Filter>();
	private EventKind eventKind;

	/**
	 * <p>
	 * Event filters are applied in the same order as they were registered by
	 * the debugger.<br/>
	 * If filter doesn't match given event, no more filters are processed and
	 * this method return immediately.<br/>
	 * This is how count filter works even though it's not clear from the
	 * specification.
	 * </p>
	 * 
	 * @param event The event to test against the request.
	 * @return Whether given event matches this request.
	 */
	public boolean matches(Event event) {
		for (Filter filter : filters) {
			if (!filter.matches(event)) {
				return false;
			}
		}

		return true;
	}
	
	public void addFilter(Filter filter) throws JdwpError {
		filter.addToEventRequest(this);
	}

	public EventKind getEventKind() {
		return eventKind;
	}

}
