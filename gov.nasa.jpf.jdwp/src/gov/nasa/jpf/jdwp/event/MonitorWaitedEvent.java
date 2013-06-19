package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

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
	 * @param threadInfo
	 *            Thread which entered monitor
	 * @param taggedObject
	 *            Monitor object reference
	 * @param location
	 *            location contended monitor enter
	 * @param timedOut
	 *            true if timed out
	 */
	public MonitorWaitedEvent(ThreadInfo threadInfo, ElementInfo taggedObject, Location location, boolean timedOut) {
		super(EventKind.MONITOR_WAITED, threadInfo, taggedObject, location);
		this.timedOut = timedOut;
	}

}
