package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Notification of a breakpoint in the target VM. The breakpoint event is
 * generated before the code at its location is executed.
 * </p>
 * 
 * @author stepan
 * 
 */
public class BreakpointEvent extends LocatableEvent implements LocationOnlyFilterable {

	/**
	 * 
	 * @param threadId
	 *            thread which hit breakpoint
	 * @param location
	 *            Location hit
	 */
	public BreakpointEvent(ThreadInfo threadInfo, Location location) {
		super(EventKind.BREAKPOINT, threadInfo, location);
	}

	@Override
	protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
	}

}
