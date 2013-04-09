package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.event.filter.ClassOnlyFilter;

public interface ClassOnlyFilterable extends Event{

	boolean matches(ClassOnlyFilter classOnlyFilter);

}
