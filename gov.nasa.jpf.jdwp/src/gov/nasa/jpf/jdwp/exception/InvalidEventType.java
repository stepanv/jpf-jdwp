package gov.nasa.jpf.jdwp.exception;

import gov.nasa.jpf.jdwp.event.Event.EventKind;

public class InvalidEventType extends JdwpError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4564896905569473797L;
	private EventKind eventKind;

	public InvalidEventType(EventKind eventKind) {
		super(ErrorType.INVALID_EVENT_TYPE);
		this.eventKind = eventKind;
	}

}
