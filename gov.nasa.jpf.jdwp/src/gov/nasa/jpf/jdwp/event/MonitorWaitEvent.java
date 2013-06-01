package gov.nasa.jpf.jdwp.event;

import java.io.DataOutputStream;
import java.io.IOException;

import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.type.Location;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Notification of a thread about to wait on a monitor object.
 * </p>
 * 
 * @since JDWP version 1.6.
 * @author stepan
 * 
 */
public class MonitorWaitEvent extends MonitorBase {

	private long timeout;

	/**
	 * Creates Monitor Wait event.
	 * 
	 * @param threadId
	 *            Thread which entered monitor
	 * @param taggedObjectId
	 *            Monitor object reference
	 * @param location
	 *            location contended monitor enter
	 * @param timeout
	 *            thread wait time in milliseconds
	 */
	public MonitorWaitEvent(ThreadId threadId, ObjectId taggedObjectId, Location location, long timeout) {
		super(EventKind.MONITOR_WAIT, threadId, taggedObjectId, location);
		this.timeout = timeout;
	}

	@Override
	protected void writeSpecific(DataOutputStream os) throws IOException {
		super.writeSpecific(os);
		os.writeLong(timeout);
	}

}
