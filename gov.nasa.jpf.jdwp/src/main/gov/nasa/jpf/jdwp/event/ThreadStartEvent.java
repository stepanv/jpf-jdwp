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

import gov.nasa.jpf.jdwp.command.VirtualMachineCommand;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Notification of a new running thread in the target VM. The new thread can be
 * the result of a call to {@link java.lang.Thread#start()} or the result of
 * attaching a new thread to the VM though JNI. The notification is generated by
 * the new thread some time before its execution starts. Because of this timing,
 * it is possible to receive other events for the thread before this event is
 * received. (Notably, Method Entry Events and Method Exit Events might occur
 * during thread initialization. It is also possible for the
 * {@link VirtualMachineCommand#ALLTHREADS} command to return a thread before
 * its thread start event is received.
 * </p>
 * <p>
 * Note that this event gives no information about the creation of the thread
 * object which may have happened much earlier, depending on the VM being
 * debugged.
 * </p>
 * 
 * @author stepan
 * 
 */
public class ThreadStartEvent extends ThreadableEvent implements Threadable {

  /**
   * Creates Thread Start event.
   * 
   * @param threadInfo
   *          Started thread
   */
  public ThreadStartEvent(ThreadInfo threadInfo) {
    super(EventKind.THREAD_START, threadInfo);
  }

  @Override
  protected void writeThreadableSpecific(DataOutputStream os) throws IOException {
  }

}
