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
import gov.nasa.jpf.jdwp.exception.NotImplementedException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * The {@link EventCommand} enum class implements the {@link CommandSet#EVENT}
 * set of commands. For the detailed specification refer to <a href=
 * "http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#JDWP_Event"
 * >http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#
 * JDWP_Event</a> JDWP 1.6 Specification pages.
 * 
 * @author stepan
 * 
 */
public enum EventCommand implements Command, ConvertibleEnum<Byte, EventCommand> {

  /**
   * The composite command is never called by the debuggers.<br/>
   * It is used as a "postponed" reply for even requests that carries out the
   * events.
   * 
   * <p>
   * <h2>JDWP Specification</h2>
   * Several events may occur at a given time in the target VM. For example,
   * there may be more than one breakpoint request for a given location or you
   * might single step to the same location as a breakpoint request. These
   * events are delivered together as a composite event. For uniformity, a
   * composite event is always used to deliver events, even if there is only one
   * event to report. <br/>
   * 
   * For the rest of the specification refer to <a href=
   * "http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#JDWP_Event_Composite"
   * >http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html
   * #JDWP_Event_Composite</a> {@link EventCommand#COMPOSITE} full
   * specification.
   * </p>
   */
  COMPOSITE(100) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      // This is really not implemented by a design!
      throw new NotImplementedException();
    }
  };
  private byte commandId;

  private EventCommand(int commandId) {
    this.commandId = (byte) commandId;
  }

  private static ReverseEnumMap<Byte, EventCommand> map = new ReverseEnumMap<Byte, EventCommand>(EventCommand.class);

  @Override
  public Byte identifier() {
    return commandId;
  }

  @Override
  public EventCommand convert(Byte val) throws IllegalArgumentException {
    return map.get(val);
  }

}