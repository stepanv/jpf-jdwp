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
 * Notification of a method invocation in the target VM. This event is generated
 * before any code in the invoked method has executed. Method entry events are
 * generated for both native and non-native methods.
 * </p>
 * <p>
 * In some VMs method entry events can occur for a particular thread before its
 * thread start event occurs if methods are called as part of the thread's
 * initialization.
 * </p>
 * 
 * @author stepan
 * 
 */
public class MethodEntryEvent extends LocatableEvent {

  /**
   * Creates Method Entry event.
   * 
   * @param threadInfo
   *          thread which entered method
   * @param location
   *          The initial executable location in the method.
   */
  public MethodEntryEvent(ThreadInfo threadInfo, Location location) {
    super(EventKind.METHOD_ENTRY, threadInfo, location);
  }

  @Override
  protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
  }

}
