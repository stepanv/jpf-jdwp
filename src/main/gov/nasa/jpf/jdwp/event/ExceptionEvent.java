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

import gov.nasa.jpf.jdwp.event.filter.ExceptionOnlyFilter;
import gov.nasa.jpf.jdwp.id.JdwpIdManager;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Notification of an exception in the target VM. If the exception is thrown
 * from a non-native method, the exception event is generated at the location
 * where the exception is thrown.<br/>
 * If the exception is thrown from a native method, the exception event is
 * generated at the first non-native location reached after the exception is
 * thrown.
 * </p>
 * 
 * @author stepan
 * 
 */
public class ExceptionEvent extends LocatableEvent implements ExceptionOnlyFilterable, LocationOnlyFilterable {

  private ElementInfo exception;
  private Location catchLocation;

  /**
   * Creates Exception event. It's interesting that catchLocation is not used by
   * JDT debugger (Eclipse) at all. It could be interesting to check how other
   * debuggers use catchLocation attribute.
   * 
   * @param threadInfo
   *          Thread with pending exception
   * @param location
   *          Location of exception throw (or first non-native location after
   *          throw if thrown from a native method)
   * @param exception
   *          Thrown exception
   * @param catchLocation
   *          Location of catch, or null (0 sent across JDWP) if not caught. An
   *          exception is considered to be caught if, at the point of the
   *          throw, the current location is dynamically enclosed in a try
   *          statement that handles the exception. (See the JVM specification
   *          for details). If there is such a try statement, the catch location
   *          is the first location in the appropriate catch clause.<br/>
   * 
   *          If there are native methods in the call stack at the time of the
   *          exception, there are important restrictions to note about the
   *          returned catch location. In such cases, it is not possible to
   *          predict whether an exception will be handled by some native method
   *          on the call stack. Thus, it is possible that exceptions considered
   *          uncaught here will, in fact, be handled by a native method and not
   *          cause termination of the target VM. Furthermore, it cannot be
   *          assumed that the catch location returned here will ever be reached
   *          by the throwing thread. If there is a native frame between the
   *          current location and the catch location, the exception might be
   *          handled and cleared in that native method instead.<br/>
   * 
   *          Note that compilers can generate try-catch blocks in some cases
   *          where they are not explicit in the source code; for example, the
   *          code generated for <code>synchronized</code> and
   *          <code>finally</code> blocks can contain implicit try-catch blocks.
   *          If such an implicitly generated try-catch is present on the call
   *          stack at the time of the throw, the exception will be considered
   *          caught even though it appears to be uncaught from examination of
   *          the source code.
   */
  public ExceptionEvent(ThreadInfo threadInfo, Location location, ElementInfo exception, Location catchLocation) {
    super(EventKind.EXCEPTION, threadInfo, location);

    this.exception = exception;
    this.catchLocation = catchLocation;
  }

  @Override
  protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
    ObjectId objectId = JdwpIdManager.getInstance().getObjectId(exception);
    objectId.writeTagged(os);
    if (catchLocation == null) {
      // TODO when location is null, we need to send twice null long ...
      // do it a better way?
      // I checked the Eclipse debugger and because of that I know what is
      // it about
      os.writeLong(0);
      os.writeLong(0);
    } else {
      catchLocation.write(os);
    }
  }

  @Override
  public boolean visit(ExceptionOnlyFilter exceptionOnlyFilter) {
    return exceptionOnlyFilter.matches(this);
  }

  /**
   * Get the exception this event is associated with.
   * 
   * @return The exception.
   */
  public ElementInfo getException() {
    return exception;
  }

  /**
   * Whether this exception event should be triggered for caught exceptions
   * only.
   * 
   * @return True or false.
   */
  public boolean isCaught() {
    return catchLocation != null;
  }

}
