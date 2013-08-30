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

package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.Threadable;
import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * <p>
 * Can be used with all {@link Threadable} events.
 * </p>
 * <p>
 * <h2>JDWP Specification</h2>
 * Restricts reported events to those in the given thread. This modifier can be
 * used with any event kind except for class unload.
 * </p>
 * 
 * @author stepan
 * 
 */
public class ThreadOnlyFilter extends Filter<Threadable> {

  private ThreadId threadId;

  /**
   * Creates Thread Only filter.
   * 
   * @param threadId
   *          Required thread
   */
  public ThreadOnlyFilter(ThreadId threadId) {
    super(ModKind.THREAD_ONLY, Threadable.class);
    this.threadId = threadId;
  }

  @Override
  public boolean matches(Threadable event) {
    ThreadInfo threadInfo;
    try {
      threadInfo = threadId.getInfoObject();
      return event.getThread() == threadInfo;
    } catch (InvalidObject e) {
      // info object is not accessible and therefore this filter is not
      // effective
      return false;
    }
  }

}
