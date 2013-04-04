package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.Event.EventKind;
import gov.nasa.jpf.jdwp.event.IEvent;
import gov.nasa.jpf.jdwp.id.object.ObjectId;

public class InstanceOnlyFilter extends Filter<IEvent> {

	private ObjectId<?> objectId;

	public InstanceOnlyFilter(ObjectId<?> objectId) {
		super(ModKind.INSTANCE_ONLY);
		this.objectId = objectId;
	}

	@Override
	protected boolean matchesInternal(IEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAllowedEventKind(EventKind eventKind) {
		switch (eventKind) {
		case CLASS_PREPARE:
		case CLASS_UNLOAD:
		case THREAD_START:
		case THREAD_END:
			return false;
		default:
			return true;
		}
	}

}
