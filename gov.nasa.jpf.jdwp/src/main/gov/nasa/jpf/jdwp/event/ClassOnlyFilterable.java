package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.event.filter.ClassOnlyFilter;
import gov.nasa.jpf.jdwp.exception.InvalidIdentifier;

public interface ClassOnlyFilterable extends Event{

	boolean matches(ClassOnlyFilter classOnlyFilter) throws InvalidIdentifier;

}
