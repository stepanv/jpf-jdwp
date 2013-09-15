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
import gov.nasa.jpf.jdwp.exception.id.InvalidIdentifierException;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * The base implementation of all events that are {@link Locatable}; that is
 * they have an associated location. <br/>
 * Note that it is assumed all the subtypes are also {@link Threadable} which
 * works for the JDWP JDK 6 specification fine. If the hierarchy of the events
 * gets little bit more crazy this would probably need a redesign.
 * 
 * @author stepan
 * 
 */
public abstract class LocatableEvent extends ThreadableEvent implements Locatable {

  private Location location;

  /**
   * Constructs the {@link LocatableEvent}.
   * 
   * @param eventKind
   *          The event kind.
   * @param threadInfo
   *          The thread associated with this event.
   * @param location
   */
  public LocatableEvent(EventKind eventKind, ThreadInfo threadInfo, Location location) {
    super(eventKind, threadInfo);

    this.location = location;
  }

  @Override
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
  public boolean matches(ClassOnlyFilter classOnlyFilter) throws InvalidIdentifierException {
    ClassInfo classInfo = location.getInstruction().getMethodInfo().getClassInfo();
    return classOnlyFilter.matches(classInfo);
  }

  /**
   * Implement this in a subtype if an additional information needs to be
   * written to the output stream.
   * 
   * @param os
   *          The stream where to write the additional info.
   * @throws IOException
   *           If an I/O error occurs.
   */
  protected abstract void writeLocatableSpecific(DataOutputStream os) throws IOException;

  @Override
  public String toString() {
    return super.toString() + ", location: " + location;
  }

}
