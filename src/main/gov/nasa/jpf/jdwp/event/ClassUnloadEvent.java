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
import gov.nasa.jpf.jdwp.value.JdwpString;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Notification of a class unload in the target VM.
 * </p>
 * <p>
 * There are severe constraints on the debugger back-end during garbage
 * collection, so unload information is greatly limited.
 * </p>
 * 
 * @author stepan
 * 
 */
public class ClassUnloadEvent extends EventBase implements ClassFilterable {

  private String signature;

  /**
   * Creates Class Unload event.
   * 
   * @param signature
   *          Type signature
   */
  public ClassUnloadEvent(String signature) {
    super(EventKind.CLASS_UNLOAD);
    this.signature = signature;
  }

  @Override
  protected void writeSpecific(DataOutputStream os) throws IOException {
    JdwpString.write(signature, os);
  }

  @Override
  public boolean matches(ClassFilter classMatchFilter) {
    return classMatchFilter.matches(signature);
  }

}
