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

import gov.nasa.jpf.jdwp.exception.id.object.InvalidThreadException;
import gov.nasa.jpf.jdwp.id.JdwpIdManager;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The base implementation for all the events that are {@link Threadable}.<br/>
 * Note that this class doesn't implement {@link Threadable} interface on a
 * purpose. It's up to the subtypes to specify whether they really are
 * threadable like.
 * 
 * @author stepan
 * 
 */
public abstract class ThreadableEvent extends EventBase implements Event {

  final static Logger logger = LoggerFactory.getLogger(ThreadableEvent.class);

  /**
   * The constructor.
   * 
   * @param eventKind
   *          The event kind.
   * @param threadInfo
   *          The thread.
   */
  public ThreadableEvent(EventKind eventKind, ThreadInfo threadInfo) {
    super(eventKind);
    this.threadInfo = threadInfo;
  }

  private ThreadInfo threadInfo;

  /**
   * Get the thread this event is associated with.
   * 
   * @return The thread.
   */
  public ThreadInfo getThread() {
    return threadInfo;
  }

  @Override
  public String toString() {
    return super.toString() + ", thread: " + threadInfo;
  }

  /**
   * This is how the threadable events add their thread related information to
   * the output stream.
   */
  protected final void writeSpecific(DataOutputStream os) throws IOException {
    ThreadId threadId = JdwpIdManager.getInstance().getThreadId(threadInfo);
    logger.debug("Thread ID: {} .. for: {}", threadId, threadInfo);
    try {
      if (threadId.get() == null || threadId.getInfoObject() != threadInfo) {
        throw new RuntimeException("Identifier for thread info instance: " + threadInfo + " is not valid.");
      }
    } catch (InvalidThreadException e) {
      throw new RuntimeException(e);
    }
    threadId.write(os);
    writeThreadableSpecific(os);
  }

  /**
   * Implement this if the subtype event needs to add more information to the
   * output stream when this event is sent across the JDWP.
   * 
   * @param os The output stream where to write the event.
   * @throws IOException If an I/O error occurs.
   */
  protected abstract void writeThreadableSpecific(DataOutputStream os) throws IOException;

}