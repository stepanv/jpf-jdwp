package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.type.Location;

import java.io.DataOutputStream;
import java.io.IOException;

public class MethodEntryEvent extends LocatableEvent {

	public MethodEntryEvent(ThreadId threadId, Location location) {
		super(EventKind.METHOD_ENTRY, threadId, location);
	}

	@Override
	protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
	}

}
