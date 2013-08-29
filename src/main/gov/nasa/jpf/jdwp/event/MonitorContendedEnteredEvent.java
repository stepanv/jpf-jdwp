package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

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
   * @param threadInfo
   *          Thread which entered monitor
   * @param taggedObject
   *          Monitor object reference
   * @param location
   *          location of contended monitor enter
   */
  public MonitorContendedEnteredEvent(ThreadInfo threadInfo, ElementInfo taggedObject, Location location) {
    super(EventKind.MONITOR_CONTENDED_ENTERED, threadInfo, taggedObject, location);
  }

}
