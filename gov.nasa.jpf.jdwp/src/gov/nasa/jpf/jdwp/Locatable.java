package gov.nasa.jpf.jdwp;

import gov.nasa.jpf.jdwp.event.ClassFilterable;
import gov.nasa.jpf.jdwp.event.ClassOnlyFilterable;
import gov.nasa.jpf.jdwp.type.Location;

public interface Locatable extends ClassFilterable, ClassOnlyFilterable {
	public Location getLocation();
}
