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

import gov.nasa.jpf.jdwp.event.filter.ClassFilter;
import gov.nasa.jpf.jdwp.event.filter.ClassOnlyFilter;
import gov.nasa.jpf.jdwp.exception.InvalidIdentifier;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class LocatableEvent extends ThreadableEvent implements Locatable {

  private Location location;

  public LocatableEvent(EventKind eventKind, ThreadInfo threadInfo, Location location) {
    super(eventKind, threadInfo);

    this.location = location;
  }

  public Location getLocation() {
    return location;
  }

  @Override
  protected void writeThreadableSpecific(DataOutputStream os) throws IOException {
    location.write(os);
    writeLocatableSpecific(os);
  }

  @Override
  public boolean matches(ClassFilter classMatchFilter) {
    String className = location.getInstruction().getMethodInfo().getClassName();
    return classMatchFilter.matches(className);
  }

  @Override
  public boolean matches(ClassOnlyFilter classOnlyFilter) throws InvalidIdentifier {
    ClassInfo classInfo = location.getInstruction().getMethodInfo().getClassInfo();
    return classOnlyFilter.matches(classInfo);
  }

  /**
   * To get the instance we need according to the JVMTI specification and the
   * OpenJDK implementation, function <tt>GetLocalInstance</tt> following
   * objects:
   * <ul>
   * <li>a thread of the frame that contains the variable at the slot 0 which is
   * a <i>this</i> object for non-static frames - as {@link Threadable} we have
   * that - checked!</li>
   * <li>to determine the depth at the stack where we're asking for the
   * <i>this</i> object - this would be 0 in this very case - the top frame</li>
   * </ul>
   * This method should be overridden by some specific events that precisely
   * redefine the general meaning of the instance.
   */
  @Override
  public ElementInfo instance() {
    ThreadInfo thread = getThread();
    StackFrame topFrame = thread.getTopFrame();
    int thisObjRef = topFrame.getThis();
    if (MJIEnv.NULL == thisObjRef) {
      // this is NULL and that is a static method
      return null;
    }
    return VM.getVM().getHeap().get(thisObjRef);
  }

  protected abstract void writeLocatableSpecific(DataOutputStream os) throws IOException;

  @Override
  public String toString() {
    return super.toString() + ", location: " + location;
  }

}
