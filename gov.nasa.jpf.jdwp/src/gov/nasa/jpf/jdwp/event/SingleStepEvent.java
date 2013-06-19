package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;

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
	 * @param threadInfo
	 *            Stepped thread
	 * @param location
	 *            Location stepped to
	 */
	public SingleStepEvent(ThreadInfo threadInfo, Location location) {
		super(EventKind.SINGLE_STEP, threadInfo, location);
	}

	@Override
	protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
	}

}
