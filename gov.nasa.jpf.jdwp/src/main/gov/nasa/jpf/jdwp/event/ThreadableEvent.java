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

import gov.nasa.jpf.jdwp.exception.InvalidThreadException;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ThreadableEvent extends EventBase implements Event {

  final static Logger logger = LoggerFactory.getLogger(ThreadableEvent.class);

  public ThreadableEvent(EventKind eventKind, ThreadInfo threadInfo) {
    super(eventKind);
    this.threadInfo = threadInfo;
  }

  private ThreadInfo threadInfo;

  public ThreadInfo getThread() {
    return threadInfo;
  }

  @Override
  public String toString() {
    return super.toString() + ", thread: " + threadInfo;
  }

  protected final void writeSpecific(DataOutputStream os) throws IOException {
    ThreadId threadId = JdwpObjectManager.getInstance().getThreadId(threadInfo);
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

  protected abstract void writeThreadableSpecific(DataOutputStream os) throws IOException;

}