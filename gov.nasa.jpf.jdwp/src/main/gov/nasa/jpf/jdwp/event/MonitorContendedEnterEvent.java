package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

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
	 * @param threadInfo
	 *            Thread which entered monitor
	 * @param location
	 *            location of contended monitor enter
	 * @param taggedObject
	 *            Monitor object reference
	 */
	public MonitorContendedEnterEvent(ThreadInfo threadInfo, ElementInfo taggedObject, Location location) {
		super(EventKind.MONITOR_CONTENDED_ENTER, threadInfo, taggedObject, location);
	}

}
