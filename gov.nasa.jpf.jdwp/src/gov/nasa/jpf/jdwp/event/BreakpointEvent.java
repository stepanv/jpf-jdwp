package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.type.Location;

public class BreakpointEvent extends LocatableEvent {

	public BreakpointEvent(Location location) {
		super(EventKind.BREAKPOINT, location);
	}

}
