package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.event.filter.ClassFilter;

public interface ClassFilterable extends Event {
	public boolean matches(ClassFilter classFilter);
}
