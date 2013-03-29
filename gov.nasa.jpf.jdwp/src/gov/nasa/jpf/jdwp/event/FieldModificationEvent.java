package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.type.Location;

import java.io.DataOutputStream;
import java.io.IOException;

public class FieldModificationEvent extends LocatableEvent {

	public FieldModificationEvent(EventKind eventKind, ThreadId threadId, Location location) {
		super(eventKind, threadId, location);
		throw new RuntimeException("NOT IMPLEMENTED YET!");
	}

	@Override
	protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
		// TODO Auto-generated method stub

	}

}
