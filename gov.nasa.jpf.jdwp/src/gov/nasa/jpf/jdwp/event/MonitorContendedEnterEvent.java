package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.type.Location;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Notification that a thread in the target VM is attempting to enter a monitor
 * that is already acquired by another thread.
 * </p>
 * 
 * @since JDWP version 1.6.
 * @author stepan
 * 
 */
public class MonitorContendedEnterEvent extends MonitorBase {

	/**
	 * Creates Monitor Contended Enter event.
	 * 
	 * @param threadId
	 *            Thread which entered monitor
	 * @param location
	 *            location of contended monitor enter
	 * @param taggedObjectId
	 *            Monitor object reference
	 */
	public MonitorContendedEnterEvent(ThreadId threadId, ObjectId taggedObjectId, Location location) {
		super(EventKind.MONITOR_CONTENDED_ENTER, threadId, taggedObjectId, location);
	}

}
