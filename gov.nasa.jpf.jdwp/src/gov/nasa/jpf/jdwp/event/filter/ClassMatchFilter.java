package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.Event.EventKind;

public class ClassMatchFilter extends Filter<Event> {

	private String classPattern;

	public ClassMatchFilter(String classPattern) {
		super(ModKind.CLASS_MATCH);
		this.classPattern = classPattern;
	}

	@Override
	protected boolean matchesInternal(Event event) {
		
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAllowedEventKind(EventKind eventKind) {
		switch (eventKind) {
			case THREAD_START:
			case THREAD_END:
				return false;
			default:
				return true;
		}
	}

}
