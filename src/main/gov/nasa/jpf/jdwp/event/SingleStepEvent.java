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
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Notification of step completion in the target VM. The step event is generated
 * before the code at its location is executed.
 * </p>
 * 
 * @author stepan
 * 
 */
public class SingleStepEvent extends LocatableEvent implements LocationOnlyFilterable, StepFilterable {

  /**
   * Creates Single Step event.
   * 
   * @param threadInfo
   *          Stepped thread
   * @param location
   *          Location stepped to
   */
  public SingleStepEvent(ThreadInfo threadInfo, Location location) {
    super(EventKind.SINGLE_STEP, threadInfo, location);
  }

  @Override
  protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
  }

}
