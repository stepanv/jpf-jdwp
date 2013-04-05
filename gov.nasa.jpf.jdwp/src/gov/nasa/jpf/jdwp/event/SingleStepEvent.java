package gov.nasa.jpf.jdwp.event;

import java.io.DataOutputStream;
import java.io.IOException;

import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.type.Location;

public class SingleStepEvent extends LocatableEvent implements LocationOnlyFilterable, StepFilterable {

	public SingleStepEvent(ThreadId threadId, Location location) {
		super(EventKind.SINGLE_STEP, threadId, location);
	}

	@Override
	protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
	}

}
