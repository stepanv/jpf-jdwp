package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.event.filter.ExceptionOnlyFilter;

public interface ExceptionOnlyFilterable extends IEvent {

	boolean visit(ExceptionOnlyFilter exceptionOnlyFilter);

}
