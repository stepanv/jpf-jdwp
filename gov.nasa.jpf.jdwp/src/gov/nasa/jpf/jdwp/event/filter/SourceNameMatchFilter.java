package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.ClassPrepareEvent;
import gov.nasa.jpf.jdwp.event.Event.EventKind;

public class SourceNameMatchFilter extends Filter<ClassPrepareEvent> {
	String sourceNamePattern;

	public SourceNameMatchFilter(String sourceNamePattern) {
		super(ModKind.SOURCE_NAME_MATCH);
		this.sourceNamePattern = sourceNamePattern;
	}

	@Override
	public boolean matches(ClassPrepareEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAllowedEventKind(EventKind eventKind) {
		switch (eventKind) {
		case CLASS_PREPARE:
			return true;
		default:
			return false;
		}
	}

}
