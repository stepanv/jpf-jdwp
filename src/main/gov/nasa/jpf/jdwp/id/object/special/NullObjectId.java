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

package gov.nasa.jpf.jdwp.id.object.special;

import gov.nasa.jpf.jdwp.id.object.ObjectIdImpl;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A special object used to represent null in SuT.<br/>
 * Remark - We don't need NullObjectId to represent special case ObjectId
 * children since null doesn't represent Thread nor Classloader nor any other
 * meaningful object.
 * 
 * @author stepan
 * 
 */
public class NullObjectId extends ObjectIdImpl {

  private NullObjectId() {
    super(Tag.OBJECT, 0, -1);
  }

  public static NullObjectId getInstance() {
    return instance;
  }

  /**
   * Helper method that writes {@link NullObjectId} instance to the stream.
   * 
   * @param os
   *          Where to write the null object
   * @throws IOException
   *           If IO error occurs
   */
  public static void instantWrite(DataOutputStream os) throws IOException {
    instance.write(os);
  }

  /**
   * Helper method that writes {@link NullObjectId} tagged instance to the
   * stream.
   * 
   * @param os
   *          Where to write the null object
   * @throws IOException
   *           If IO error occurs
   */
  public static void instanceWriteTagged(DataOutputStream os) throws IOException {
    instance.writeTagged(os);
  }

  private static final NullObjectId instance = new NullObjectId();

  @Override
  public void push(StackFrame frame) {
    frame.pushRef(-1);
  }

}
