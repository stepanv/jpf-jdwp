/* 
   Copyright (C) 2013 Stepan Vavra

This file is part of (Java Debug Wire Protocol) JDWP for 
Java PathFinder (JPF) project.

JDWP for JPF is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

JDWP for JPF is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 
 */

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
   *          Thread which entered monitor
   * @param location
   *          location of contended monitor enter
   * @param taggedObject
   *          Monitor object reference
   */
  public MonitorContendedEnterEvent(ThreadInfo threadInfo, ElementInfo taggedObject, Location location) {
    super(EventKind.MONITOR_CONTENDED_ENTER, threadInfo, taggedObject, location);
  }

}
