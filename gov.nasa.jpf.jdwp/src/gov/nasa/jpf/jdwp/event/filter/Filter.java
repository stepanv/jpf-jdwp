package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.EventRequest;
import gov.nasa.jpf.jdwp.event.Event.EventKind;
import gov.nasa.jpf.jdwp.exception.IllegalArgumentException;
import gov.nasa.jpf.jdwp.exception.JdwpException;

/**
 * 
 * Constraints used to control the number of generated events. Modifiers specify
 * additional tests that an event must satisfy before it is placed in the event
 * queue. Events are filtered by applying each modifier to an event in the order
 * they are specified in this collection Only events that satisfy all modifiers
 * are reported. A value of 0 means there are no modifiers in the request.
 * 
 * Filtering can improve debugger performance dramatically by reducing the
 * amount of event traffic sent from the target VM to the debugger VM.
 * 
 * @author stepan
 * 
 */
public abstract class Filter<T> {
	
	public enum ModKind {
		COUNT,
		CONDITIONAL,
		THREAD_ONLY,
		CLASS_ONLY,
		CLASS_MATCH,
		CLASS_EXCLUDE,
		LOCATION_ONLY,
		EXCEPTION_ONLY,
		FIELD_ONLY,
		STEP,
		INSTANCE_ONLY,
		SOURCE_NAME_MATCH
	}

	private ModKind modKind;
	
	public Filter(ModKind modKind) {
		this.modKind = modKind;
	}

	public abstract boolean matches(T event);

	public abstract boolean isAllowedEventKind(EventKind eventKind);
	
	public void addToEventRequest(EventRequest eventRequest) throws JdwpException {
		if (isAllowedEventKind(eventRequest.getEventKind())) {
			eventRequest.addFilter(this);
		} else 		{
			throw new IllegalArgumentException();
		}

	}

}
