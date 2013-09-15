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

import gov.nasa.jpf.jdwp.exception.JdwpException;
import gov.nasa.jpf.jdwp.exception.IllegalArgumentException;
import gov.nasa.jpf.jdwp.id.object.ArrayId;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.jdwp.value.ValueUtils;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * The {@link ArrayReferenceCommand} enum class implements the
 * {@link CommandSet#ARRAYREFERENCE} set of commands. For the detailed
 * specification refer to <a href=
 * "http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#JDWP_ArrayReference"
 * >http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#
 * JDWP_ArrayReference</a> JDWP 1.6 Specification pages.
 * 
 * @author stepan
 * 
 */
public enum ArrayReferenceCommand implements Command, ConvertibleEnum<Byte, ArrayReferenceCommand> {

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the number of components in a given array.
   * </p>
   */
  LENGTH(1) {
    @Override
    public void execute(ElementInfo array, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
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
    public void execute(ElementInfo array, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
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
    public void execute(ElementInfo array, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
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
  public ArrayReferenceCommand convert(Byte val) throws IllegalArgumentException {
    return map.get(val);
  }

  /**
   * The array reference command specific implementation.
   * 
   * @param array
   *          The array referenced in the command.
   * @param bytes
   *          The buffer of bytes that is used as an input of the command.
   * @param os
   *          The output stream that is used for a command output.
   * @param contextProvider
   *          The Context Provider.
   * @throws IOException
   *           If given input or output have I/O issues.
   * @throws JdwpException
   *           If any JDWP based error occurs.
   */
  public abstract void execute(ElementInfo array, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException;

  @Override
  public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
    ArrayId arrayId = contextProvider.getObjectManager().readArrayId(bytes);
    execute(arrayId.get(), bytes, os, contextProvider);

  }
}