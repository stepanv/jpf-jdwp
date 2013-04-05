package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.type.Location;

/**
 * 
 * @author stepan
 *
 */
public interface Locatable extends Threadable, ClassFilterable, ClassOnlyFilterable, InstanceOnlyFilterable {
	public Location getLocation();
}
