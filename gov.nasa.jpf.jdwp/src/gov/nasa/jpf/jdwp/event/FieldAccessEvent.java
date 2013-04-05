package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.type.Location;

import java.io.DataOutputStream;
import java.io.IOException;

public class FieldAccessEvent extends LocatableEvent implements LocationOnlyFilterable, FieldOnlyFilterable {

	public FieldAccessEvent(ThreadId threadId, Location location) {
		super(EventKind.FIELD_ACCESS, threadId, location);
		throw new RuntimeException("NOT IMPLEMENTED YET!");
	}

	@Override
	protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
		// TODO Auto-generated method stub

	}

}
