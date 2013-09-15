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
import gov.nasa.jpf.jdwp.id.object.StringId;
import gov.nasa.jpf.jdwp.value.JdwpString;
import gov.nasa.jpf.vm.ElementInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * The {@link StringReferenceCommand} enum class implements the
 * {@link CommandSet#STRINGREFERENCE} set of commands. For the detailed
 * specification refer to <a href=
 * "http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#JDWP_StringReference"
 * >http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#
 * JDWP_StringReference</a> JDWP 1.6 Specification pages.
 * 
 * @author stepan
 * 
 */
public enum StringReferenceCommand implements Command, ConvertibleEnum<Byte, StringReferenceCommand> {

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the characters contained in the string.
   * </p>
   */
  VALUE(1) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
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
  public StringReferenceCommand convert(Byte val) throws IllegalArgumentException {
    return map.get(val);
  }

}