package gov.nasa.jpf.jdwp.event;

import java.io.DataOutputStream;
import java.io.IOException;

import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.type.Location;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Notification of step completion in the target VM. The step event is generated
 * before the code at its location is executed.
 * </p>
 * 
 * @author stepan
 * 
 */
public class SingleStepEvent extends LocatableEvent implements LocationOnlyFilterable, StepFilterable {

	/**
	 * Creates Single Step event.
	 * 
	 * @param threadId
	 *            Stepped thread
	 * @param location
	 *            Location stepped to
	 */
	public SingleStepEvent(ThreadId threadId, Location location) {
		super(EventKind.SINGLE_STEP, threadId, location);
	}

	@Override
	protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
	}

}
