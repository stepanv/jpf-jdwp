package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.type.Location;

import java.io.DataOutputStream;
import java.io.IOException;

public class MonitorBase extends LocatableEvent {

	private ObjectId taggedObjectId;

	public MonitorBase(EventKind eventKind, ThreadId threadId, ObjectId taggedObjectId, Location location) {
		super(eventKind, threadId, location);
		this.taggedObjectId = taggedObjectId;
	}

	@Override
	protected void writeSpecific(DataOutputStream os) throws IOException {
		taggedObjectId.write(os);
		getLocation().write(os);
		writeLocatableSpecific(os);
	}

	@Override
	protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
	}

}
