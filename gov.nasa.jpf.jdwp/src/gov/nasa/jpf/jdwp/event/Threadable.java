package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.id.object.ThreadId;

public interface Threadable extends IEvent {
	public ThreadId getThread();
}
