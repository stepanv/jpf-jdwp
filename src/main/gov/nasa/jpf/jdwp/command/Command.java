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

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * The {@link Command} interface specifies the command API of all the JDWP
 * commands.
 * 
 * @author stepan
 * 
 */
public interface Command {

  /**
   * The implementation of the command.
   * 
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
  public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException;

  /**
   * The command ID used across the JDWP which must be unique in it's
   * {@link CommandSet}.</br> By this ID the debugger specifies which command to
   * execute.
   * 
   * @return The command ID.
   */
  Byte identifier();
}
