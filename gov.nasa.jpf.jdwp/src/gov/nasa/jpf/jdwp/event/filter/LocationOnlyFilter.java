package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.Event.EventKind;
import gov.nasa.jpf.jdwp.event.LocatableEvent;
import gov.nasa.jpf.jdwp.type.Location;

public class LocationOnlyFilter extends Filter<LocatableEvent> {

	private Location location;

	public LocationOnlyFilter(Location location) {
		super(Filter.ModKind.LOCATION_ONLY);
		
		this.location = location;
	}

	@Override
	public boolean matches(LocatableEvent event) {
		return location == event.getLocation();
	}

	@Override
	public boolean isAllowedEventKind(EventKind eventKind) {
		switch(eventKind) {
		case BREAKPOINT:
		case FIELD_ACCESS:
		case FIELD_MODIFICATION:
		case SINGLE_STEP:
		case EXCEPTION:
			return true;
		default:
			return false;
		}
	}

}
