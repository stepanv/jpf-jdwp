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

import java.io.DataOutputStream;
import java.io.IOException;

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
   *          Thread which entered monitor
   * @param taggedObject
   *          Monitor object reference
   * @param location
   *          location contended monitor enter
   * @param timedOut
   *          true if timed out
   */
  public MonitorWaitedEvent(ThreadInfo threadInfo, ElementInfo taggedObject, Location location, boolean timedOut) {
    super(EventKind.MONITOR_WAITED, threadInfo, taggedObject, location);
    this.timedOut = timedOut;
  }
  
  @Override
  protected void writeThreadableSpecific(DataOutputStream os) throws IOException {
    super.writeThreadableSpecific(os);
    os.writeBoolean(timedOut);
  }

}
