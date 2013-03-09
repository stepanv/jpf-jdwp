package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.type.Location;

public class ExceptionEvent extends LocatableEvent {

	public ExceptionEvent(Location location) {
		super(EventKind.EXCEPTION, location);
	}

}
