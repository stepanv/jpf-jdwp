package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.type.Location;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Notification of a thread in the target VM is entering a monitor after waiting
 * for it to be released by another thread.
 * </p>
 * 
 * @since JDWP version 1.6.
 * @author stepan
 * 
 */
public class MonitorContendedEnteredEvent extends MonitorBase {

	/**
	 * Creates Monitor Contended Entered event.
	 * 
	 * @param threadId
	 *            Thread which entered monitor
	 * @param taggedObjectId
	 *            Monitor object reference
	 * @param location
	 *            location of contended monitor enter
	 */
	public MonitorContendedEnteredEvent(ThreadId threadId, ObjectId taggedObjectId, Location location) {
		super(EventKind.MONITOR_CONTENDED_ENTERED, threadId, taggedObjectId, location);
	}

}
