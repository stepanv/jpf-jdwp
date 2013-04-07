package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.type.Location;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Notification that a thread in the target VM has finished waiting on a monitor
 * object.
 * </p>
 * 
 * @since JDWP version 1.6.
 * @author stepan
 * 
 */
public class MonitorWaitedEvent extends MonitorBase {

	private boolean timedOut;

	/**
	 * Creates Monitor Waited event.
	 * 
	 * @param threadId
	 *            Thread which entered monitor
	 * @param taggedObjectId
	 *            Monitor object reference
	 * @param location
	 *            location contended monitor enter
	 * @param timedOut
	 *            true if timed out
	 */
	public MonitorWaitedEvent(ThreadId threadId, ObjectId taggedObjectId, Location location, boolean timedOut) {
		super(EventKind.MONITOR_WAITED, threadId, taggedObjectId, location);
		this.timedOut = timedOut;
	}

}
