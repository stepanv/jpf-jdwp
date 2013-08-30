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

import java.io.DataOutputStream;
import java.io.IOException;

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
   * @param threadInfo
   *          Thread which entered monitor
   * @param taggedObject
   *          Monitor object reference
   * @param location
   *          location contended monitor enter
   * @param timeout
   *          thread wait time in milliseconds
   */
  public MonitorWaitEvent(ThreadInfo threadInfo, ElementInfo taggedObject, Location location, long timeout) {
    super(EventKind.MONITOR_WAIT, threadInfo, taggedObject, location);
    this.timeout = timeout;
  }

  @Override
  protected void writeThreadableSpecific(DataOutputStream os) throws IOException {
    super.writeThreadableSpecific(os);
    os.writeLong(timeout);
  }

}
