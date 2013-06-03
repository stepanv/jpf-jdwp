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

	/**
	 * Overrides threadable specific write since we have to write tagged Object
	 * Id before it's actual location.
	 */
	@Override
	protected void writeThreadableSpecific(DataOutputStream os) throws IOException {
		taggedObjectId.write(os);
		getLocation().write(os);

	}

	@Override
	protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
		//empty
	}

}
