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
 * Notification of a method return in the target VM. This event is generated
 * after all code in the method has executed, but the location of this event is
 * the last executed location in the method. Method exit events are generated
 * for both native and non-native methods. Method exit events are not generated
 * if the method terminates with a thrown exception.
 * </p>
 * 
 * @author stepan
 * 
 */
public class MethodExitEvent extends LocatableEvent {

  /**
   * 
   * @param threadInfo
   *          The thread which exited method
   * @param location
   *          Location of exit
   */
  public MethodExitEvent(ThreadInfo threadInfo, Location location) {
    super(EventKind.METHOD_EXIT, threadInfo, location);
  }

  @Override
  protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
  }

}
