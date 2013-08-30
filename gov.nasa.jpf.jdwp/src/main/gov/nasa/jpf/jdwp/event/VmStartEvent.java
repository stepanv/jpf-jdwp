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

import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Notification of initialization of a target VM. This event is received before
 * the main thread is started and before any application code has been executed.
 * Before this event occurs a significant amount of system code has executed and
 * a number of system classes have been loaded. This event is always generated
 * by the target VM, even if not explicitly requested.
 * </p>
 * 
 * @author stepan
 * 
 */
public class VmStartEvent extends ThreadableEvent implements Threadable {

  /**
   * Creates VM Start event.
   * 
   * @param currentThread
   *          The initial thread of the VM.
   */
  public VmStartEvent(ThreadInfo currentThread) {
    super(EventKind.VM_START, currentThread);
  }

  @Override
  protected void writeThreadableSpecific(DataOutputStream os) throws IOException {
  }

}
