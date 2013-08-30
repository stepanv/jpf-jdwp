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
import gov.nasa.jpf.jdwp.id.object.StringId;
import gov.nasa.jpf.jdwp.value.JdwpString;
import gov.nasa.jpf.vm.ElementInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum StringReferenceCommand implements Command, ConvertibleEnum<Byte, StringReferenceCommand> {
  VALUE(1) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
      StringId stringId = contextProvider.getObjectManager().readStringId(bytes);
      ElementInfo elementInfo = stringId.get();
      JdwpString.write(elementInfo.asString(), os);
    }
  };
  private byte commandId;

  private StringReferenceCommand(int commandId) {
    this.commandId = (byte) commandId;
  }

  private static ReverseEnumMap<Byte, StringReferenceCommand> map = new ReverseEnumMap<Byte, StringReferenceCommand>(
      StringReferenceCommand.class);

  @Override
  public Byte identifier() {
    return commandId;
  }

  @Override
  public StringReferenceCommand convert(Byte val) throws JdwpError {
    return map.get(val);
  }

  @Override
  public abstract void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;
}