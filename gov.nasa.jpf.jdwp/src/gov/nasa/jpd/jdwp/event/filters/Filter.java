package gov.nasa.jpd.jdwp.event.filters;

import gov.nasa.jpd.jdwp.event.Event;

public abstract class Filter {

	public abstract <T extends Event> boolean matches(T event);

}
