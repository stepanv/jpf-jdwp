package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.type.Location;


public class SingleStepEvent extends LocatableEvent {

	public SingleStepEvent(Location location) {
		super(EventKind.SINGLE_STEP, location);
	}

}
