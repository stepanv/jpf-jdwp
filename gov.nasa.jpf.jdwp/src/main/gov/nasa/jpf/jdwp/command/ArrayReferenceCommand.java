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

package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.id.object.ArrayId;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.jdwp.value.ValueUtils;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum ArrayReferenceCommand implements Command, ConvertibleEnum<Byte, ArrayReferenceCommand> {

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the number of components in a given array.
   * </p>
   */
  LENGTH(1) {
    @Override
    public void execute(ElementInfo array, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {
      os.writeInt(array.arrayLength());
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns a range of array components. The specified range must be within the
   * bounds of the array.
   * </p>
   * <p>
   * Known use-cases:
   * <ul>
   * <li>inspecting an array</li>
   * </ul>
   * </p>
   */
  GETVALUES(2) {
    @Override
    public void execute(ElementInfo array, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {
      int first = bytes.getInt();
      int length = bytes.getInt();

      // get the component type first
      ClassInfo componentClassInfo = array.getClassInfo().getComponentClassInfo();

      os.writeByte(Tag.classInfoToTag(componentClassInfo).identifier());
      os.writeInt(length);

      for (int i = first; i < first + length; i++) {
        Value value = ValueUtils.arrayIndexToValue(array, i);
        if (componentClassInfo.isPrimitive()) {
          value.writeUntagged(os);
        } else {
          value.writeTagged(os);
        }
      }

    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Sets a range of array components. The specified range must be within the
   * bounds of the array. For primitive values, each value's type must match the
   * array component type exactly. For object values, there must be a widening
   * reference conversion from the value's type to the array component type and
   * the array component type must be loaded.
   * </p>
   */
  SETVALUES(3) {
    @Override
    public void execute(ElementInfo array, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {
      int first = bytes.getInt();
      int values = bytes.getInt();

      ClassInfo componentClassInfo = array.getClassInfo().getComponentClassInfo();
      Tag tag = Tag.classInfoToTag(componentClassInfo);

      for (int i = first; i < first + values; ++i) {
        Value valueUntagged = tag.readValue(bytes);
        valueUntagged.modify(array.getModifiableInstance(), i);
      }
    }
  };

  private byte commandId;

  private ArrayReferenceCommand(int commandId) {
    this.commandId = (byte) commandId;
  }

  private static ReverseEnumMap<Byte, ArrayReferenceCommand> map = new ReverseEnumMap<Byte, ArrayReferenceCommand>(
      ArrayReferenceCommand.class);

  @Override
  public Byte identifier() {
    return commandId;
  }

  @Override
  public ArrayReferenceCommand convert(Byte val) throws JdwpError {
    return map.get(val);
  }

  public abstract void execute(ElementInfo array, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
      throws IOException, JdwpError;

  @Override
  public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
    ArrayId arrayId = contextProvider.getObjectManager().readArrayId(bytes);
    execute(arrayId.get(), bytes, os, contextProvider);

  }
}