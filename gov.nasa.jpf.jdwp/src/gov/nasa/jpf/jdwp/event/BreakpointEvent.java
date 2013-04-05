package gov.nasa.jpf.jdwp.event;

import java.io.DataOutputStream;
import java.io.IOException;

import gov.nasa.jpf.jdwp.event.filter.ClassOnlyFilter;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.type.Location;

public class BreakpointEvent extends LocatableEvent implements LocationOnlyFilterable {

	public BreakpointEvent(ThreadId threadId, Location location) {
		super(EventKind.BREAKPOINT, threadId, location);
	}

	@Override
	protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
	}

}
