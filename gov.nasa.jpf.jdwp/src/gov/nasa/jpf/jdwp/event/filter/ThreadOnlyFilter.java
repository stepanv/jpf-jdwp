package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.Event.EventKind;
import gov.nasa.jpf.jdwp.id.object.ThreadId;

public class ThreadOnlyFilter extends Filter<Event> {

	private ThreadId threadId;

	public ThreadOnlyFilter(ThreadId threadId) {
		super(ModKind.THREAD_ONLY);
		this.threadId = threadId;
	}

	@Override
	public boolean matchesInternal(Event event) {
		return event.getThread() == threadId;
	}

	@Override
	public boolean isAllowedEventKind(EventKind eventKind) {
		switch(eventKind) {
		case CLASS_UNLOAD:
			return false;
		default:
			return true;
		}
	}
}
