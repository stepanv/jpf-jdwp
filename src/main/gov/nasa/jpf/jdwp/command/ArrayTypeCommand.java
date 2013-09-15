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
import gov.nasa.jpf.jdwp.id.type.ArrayTypeReferenceId;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Heap;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Types;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * The {@link ArrayTypeCommand} enum class implements the
 * {@link CommandSet#ARRAYTYPE} set of commands. For the detailed specification
 * refer to <a href=
 * "http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#JDWP_ArrayType"
 * >http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#
 * JDWP_ArrayType</a> JDWP 1.6 Specification pages.
 * 
 * @author stepan
 * 
 */
public enum ArrayTypeCommand implements Command, ConvertibleEnum<Byte, ArrayTypeCommand> {

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Creates a new array object of this type with a given length.
   * </p>
   * <p>
   * <h2>JPF specifics</h2>
   * JPF needs a thread to be able to create a new array; however, JPWP
   * specification does not.<br/>
   * Therefore a current thread is used which should work just fine.
   * </p>
   */
  NEWINSTANCE(1) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      ArrayTypeReferenceId arrayTypeId = contextProvider.getObjectManager().readArrayTypeReferenceId(bytes);
      int length = bytes.getInt();

      ClassInfo componentClassInfo = arrayTypeId.get().getComponentClassInfo();
      Heap heap = contextProvider.getVM().getHeap();

      // the JDWP specification doesn't expect VM to associate an array creation with some thread
      // and since JPF needs a thread we have to use any available one
      ThreadInfo threadInfo = contextProvider.getVM().getCurrentThread();
      int typeCode = Types.getTypeCode(componentClassInfo.getSignature());
      
      // this works for primitive types
      String type = Types.getElementDescriptorOfType(typeCode);
      if (type == null) {
        // this works for references ... it is so weird!
        type = componentClassInfo.getType();
      }

      ElementInfo elementInfoArray = heap.newArray(type, length, threadInfo);
      ArrayId arrayId = contextProvider.getObjectManager().getArrayId(elementInfoArray);
      arrayId.writeTagged(os);

    }
  };
  private byte commandId;

  private ArrayTypeCommand(int commandId) {
    this.commandId = (byte) commandId;
  }

  private static ReverseEnumMap<Byte, ArrayTypeCommand> map = new ReverseEnumMap<Byte, ArrayTypeCommand>(ArrayTypeCommand.class);

  @Override
  public Byte identifier() {
    return commandId;
  }

  @Override
  public ArrayTypeCommand convert(Byte val) throws IllegalArgumentException {
    return map.get(val);
  }

}