package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.Locatable;
import gov.nasa.jpf.jdwp.type.Location;

public abstract class LocatableEvent extends Event implements Locatable {

	private Location location;

	public LocatableEvent(EventKind eventKind, Location location) {
		super(eventKind);
		
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}

}
