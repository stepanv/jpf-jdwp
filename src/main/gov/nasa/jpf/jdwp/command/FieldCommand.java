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

import gov.nasa.jpf.jdwp.exception.IllegalArgumentException;
import gov.nasa.jpf.jdwp.exception.JdwpException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * The {@link FieldCommand} enum class implements the {@link CommandSet#FIELD}
 * set of commands. For the detailed specification refer to <a href=
 * "http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#JDWP_Field"
 * >http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#
 * JDWP_Field</a> JDWP 1.6 Specification pages.
 * <p>
 * Note that there is no specified command even in the JDWP JDK 7 Specification.
 * </p>
 * 
 * @author stepan
 * 
 */
public enum FieldCommand implements Command, ConvertibleEnum<Byte, FieldCommand> {
  NONE;

  private static ReverseEnumMap<Byte, FieldCommand> map = new ReverseEnumMap<Byte, FieldCommand>(FieldCommand.class);

  @Override
  public Byte identifier() {
    return null;
  }

  @Override
  public FieldCommand convert(Byte val) throws IllegalArgumentException {
    return map.get(val);
  }

  @Override
  public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
  }
}