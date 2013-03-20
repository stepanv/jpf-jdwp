package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.Event.EventKind;
import gov.nasa.jpf.jdwp.event.LocatableEvent;
import gov.nasa.jpf.jdwp.id.object.ThreadId;

public class ThreadOnlyFilter extends Filter<LocatableEvent> {

	private ThreadId threadId;

	public ThreadOnlyFilter(ThreadId threadId) {
		super(ModKind.THREAD_ONLY);
		this.threadId = threadId;
	}

	@Override
	public boolean matches(LocatableEvent event) {
		if (!isAllowedEventKind(event.getEventKind())) {
			return false;
		}
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
