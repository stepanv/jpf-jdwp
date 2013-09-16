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

package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.exception.id.object.InvalidThreadException;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadList;
import gov.nasa.jpf.vm.VM;

/**
 * The {@link ThreadId} implementation.
 * 
 * @see ThreadId
 * 
 * @author stepan
 * 
 */
public class ThreadIdImpl extends ObjectIdImpl implements ThreadId {

  public ThreadIdImpl(long id, ThreadInfo threadInfo) {
    this(id, threadInfo.getThreadObject(), threadInfo);
  }

  private ThreadIdImpl(long id, ElementInfo elementInfo, ThreadInfo threadInfo) {
    super(Tag.THREAD, id, elementInfo);
  }

  public ThreadIdImpl(long id, ElementInfo elementInfo) {
    this(id, elementInfo, getThreadInfo(elementInfo));
  }

  /**
   * Always resolves the info object because {@link ThreadInfo} instances do
   * change during the SuT execution.
   * 
   * @return Resolved Thread Info instance.
   */
  @Override
  public ThreadInfo getThreadInfo() throws InvalidThreadException {
    return resolveInfoObject();
  }

  private ThreadInfo resolveInfoObject() throws InvalidThreadException {
    ElementInfo threadElementInfo = get();
    if (threadElementInfo == null) {
      throw new InvalidThreadException(this);
    }
    ThreadInfo threadInfo = getThreadInfo(threadElementInfo);
    if (threadInfo == null) {
      throw new InvalidThreadException(this);
    }
    return threadInfo;
  }

  /**
   * TODO this method may return null .. i.e. solve the case when there is not
   * related ThreadInfo .. it's also possible it was an exceptional state when
   * this happened - the code contained compilation error
   * 
   * @param elementInfo
   * @return
   */
  private static ThreadInfo getThreadInfo(ElementInfo elementInfo) {
    ThreadList threadList = VM.getVM().getThreadList();
    return threadList.getThreadInfoForObjRef(elementInfo.getObjectRef());
  }

}
