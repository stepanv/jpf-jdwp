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

import gnu.classpath.jdwp.Jdwp;
import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.EventBase.EventKind;
import gov.nasa.jpf.jdwp.event.EventRequest;
import gov.nasa.jpf.jdwp.exception.IllegalArgumentException;
import gov.nasa.jpf.jdwp.exception.JdwpException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * The {@link EventRequestCommand} enum class implements the
 * {@link CommandSet#EVENTREQUEST} set of commands. For the detailed
 * specification refer to <a href=
 * "http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#JDWP_EventRequest"
 * >http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#
 * JDWP_EventRequest</a> JDWP 1.6 Specification pages.
 * 
 * @author stepan
 * 
 */
public enum EventRequestCommand implements Command, ConvertibleEnum<Byte, EventRequestCommand> {

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Set an event request. When the event described by this request occurs, an
   * {@link Event} is sent from the target VM. If an event occurs that has not
   * been requested then it is not sent from the target VM. The two exceptions
   * to this are the {@link EventKind#VM_START} Event and the
   * {@link EventKind#VM_DEATH} Event which are automatically generated events.<br/>
   * See {@link EventCommand#COMPOSITE} for further details.
   * </p>
   * 
   * @see Event
   * @see EventCommand#COMPOSITE
   * @see EventRequest
   */
  SET(1) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      EventRequest<Event> eventRequest = EventRequest.factory(bytes, contextProvider);

      Jdwp.getEventRequestManager().requestEvent(eventRequest);
      contextProvider.getVirtualMachine().registerEventRequest(eventRequest);

      os.writeInt(eventRequest.getId());
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Clear an event request. See {@link EventKind} for a complete list of events
   * that can be cleared. Only the event request matching the specified event
   * kind and requestID is cleared. If there isn't a matching event request the
   * command is a no-op and does not result in an error. Automatically generated
   * events do not have a corresponding event request and may not be cleared
   * using this command.
   * </p>
   */
  CLEAR(2) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      EventKind eventKind = EventKind.BREAKPOINT.convert(bytes.get());
      Jdwp.getEventRequestManager().removeEventRequest(eventKind, bytes.getInt());

    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Removes all set breakpoints, a no-op if there are no breakpoints set.
   * </p>
   */
  CLEARALLBREAKPOINTS(3) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      Jdwp.getEventRequestManager().clearEventRequests(EventKind.BREAKPOINT);
    }
  };
  private byte commandId;

  private EventRequestCommand(int commandId) {
    this.commandId = (byte) commandId;
  }

  private static ReverseEnumMap<Byte, EventRequestCommand> map = new ReverseEnumMap<Byte, EventRequestCommand>(EventRequestCommand.class);

  @Override
  public Byte identifier() {
    return commandId;
  }

  @Override
  public EventRequestCommand convert(Byte val) throws IllegalArgumentException {
    return map.get(val);
  }

  @Override
  public abstract void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException;
}