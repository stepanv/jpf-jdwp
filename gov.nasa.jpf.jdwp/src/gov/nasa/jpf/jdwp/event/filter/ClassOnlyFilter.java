package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.Event.EventKind;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;

public class ClassOnlyFilter extends Filter<Event> {

	private ReferenceTypeId referenceTypeId;

	public ClassOnlyFilter(ReferenceTypeId referenceTypeId) {
		super(ModKind.CLASS_ONLY);
		this.referenceTypeId = referenceTypeId;
	}

	@Override
	protected boolean matchesInternal(Event event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAllowedEventKind(EventKind eventKind) {
		switch (eventKind) {
		case CLASS_UNLOAD:
		case THREAD_START:
		case THREAD_END:
			return false;
		default:
			return true;
		}
	}

}
