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

package gov.nasa.jpf.jdwp.event;

import gnu.classpath.jdwp.transport.JdwpCommandPacket;
import gov.nasa.jpf.jdwp.exception.IllegalArgumentException;
import gnu.classpath.jdwp.transport.JdwpPacket;
import gov.nasa.jpf.jdwp.command.CommandSet;
import gov.nasa.jpf.jdwp.command.ConvertibleEnum;
import gov.nasa.jpf.jdwp.command.EventCommand;
import gov.nasa.jpf.jdwp.command.ReverseEnumMap;
import gov.nasa.jpf.jdwp.event.EventRequest.SuspendPolicy;
import gov.nasa.jpf.jdwp.event.filter.Filter;
import gov.nasa.jpf.jdwp.exception.InvalidEventTypeException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The base implementation of all events.<br/>
 * All the common functionality of all events should go here.
 * 
 * <p>
 * Remark - see the discussion about unused event kinds in the {@link EventKind}
 * enum.
 * </p>
 * 
 * @see EventKind
 * 
 * @author stepan
 * 
 */
public abstract class EventBase implements Event {

  private EventKind eventKind;
  private List<EventRequest<? extends Event>> matchingEventRequests = new LinkedList<EventRequest<? extends Event>>();

  public EventBase(EventKind eventKind) {
    this.eventKind = eventKind;
  }

  @Override
  public EventKind getEventKind() {
    return eventKind;
  }

  @SuppressWarnings("unchecked")
  public <T extends Event> boolean addIfMatches(EventRequest<T> eventRequest) {
    // this casts sucks
    if (eventRequest.matches((T) this)) {
      matchingEventRequests.add(eventRequest);
      return true;
    }
    return false;
  }

  @Override
  public List<EventRequest<? extends Event>> matchingEventRequests() {
    return matchingEventRequests;
  }

  @Override
  public final void write(DataOutputStream os, int requestId) throws IOException {
    os.writeByte(eventKind.identifier());
    os.writeInt(requestId);
    writeSpecific(os);
  }

  /**
   * This is how subtypes of this class write their additional information to
   * the output stream.
   * 
   * @param os
   *          The output stream
   * @throws IOException
   *           If an I/O error occurs.
   */
  protected abstract void writeSpecific(DataOutputStream os) throws IOException;

  @Override
  public String toString() {
    return "Event: " + super.toString() + ", kind: " + eventKind;
  }

  /**
   * All the kinds of events implemented as {@link ConvertibleEnum} enums so
   * that they can be easily translated from their corresponding IDs.
   * 
   * <p>
   * Some of the following event kinds do not have specified what they mean and
   * technically cannot be used since there is no specification for them.<br/>
   * Refer to the {@link EventKind#EventKind(int)} constructor and to the
   * {@link EventKind#isFilterableBy(Filter)} method where an exception might be
   * thrown in some situations.
   * </p>
   * 
   * @author stepan
   * 
   */
  public static enum EventKind implements ConvertibleEnum<Byte, EventKind> {
    /** Never sent across JDWP */
    VM_DISCONNECTED(100),
    /** Single step event kind */
    SINGLE_STEP(1, SingleStepEvent.class),
    /** Breakpoint hit event kind */
    BREAKPOINT(2, BreakpointEvent.class),
    /** Frame pop event kind */
    FRAME_POP(3),
    /** Exception thrown event kind */
    EXCEPTION(4, ExceptionEvent.class),
    /** User defined event kind */
    USER_DEFINED(5),
    /** Thread start event kind */
    THREAD_START(6, ThreadStartEvent.class),

    /** @see EventKind#THREAD_END */
    THREAD_DEATH(7, ThreadDeathEvent.class),

    /** Class prepare event kind */
    CLASS_PREPARE(8, ClassPrepareEvent.class),
    /** Class unload event kind */
    CLASS_UNLOAD(9, ClassUnloadEvent.class),
    /** Class load event kind */
    CLASS_LOAD(10),
    /** Field access event kind */
    FIELD_ACCESS(20, FieldAccessEvent.class),
    /** Field modification event kind */
    FIELD_MODIFICATION(21, FieldModificationEvent.class),
    /** Exception catch event kind */
    EXCEPTION_CATCH(30),
    /** Method entry event kind */
    METHOD_ENTRY(40, MethodEntryEvent.class),
    /** Method exit event kind */
    METHOD_EXIT(41, MethodExitEvent.class),
    /** Method exit with a return value event kind */
    METHOD_EXIT_WITH_RETURN_VALUE(42, MethodExitWithReturnValueEvent.class),
    /** Monitor contended enter event kind */
    MONITOR_CONTENDED_ENTER(43, MonitorContendedEnterEvent.class),
    /** Monitor contended entered event kind */
    MONITOR_CONTENDED_ENTERED(44, MonitorContendedEnteredEvent.class),
    /** Monitor wait event kind */
    MONITOR_WAIT(45, MonitorWaitEvent.class),
    /** Monitor waited event kind */
    MONITOR_WAITED(46, MonitorWaitedEvent.class),

    /** @see EventKind#VM_INIT */
    VM_START(90, VmStartEvent.class),
    /** VM death event kind */
    VM_DEATH(99, VmDeathEvent.class),

    /** obsolete - was used in jvmdi */
    VM_INIT(VM_START),
    /** obsolete - was used in jvmdi */
    THREAD_END(THREAD_DEATH);

    private byte eventId;
    private Class<? extends Event> eventClass;

    /**
     * This constructor is here just to be able to create all the defined
     * events. However, several of the defined events are not mentioned in the
     * specification and thus cannot be used!
     * 
     * @see EventKind#isFilterableBy(Filter)
     * @param eventId
     *          The ID of this event kind.
     */
    EventKind(int eventId) {
      this.eventId = (byte) eventId;
      this.eventClass = null;
    }

    /**
     * The proper constructor of the event kind enum.
     * 
     * @param eventId
     *          The ID of the event kind.
     * @param eventClass
     *          The class this event kind is associated with.
     */
    EventKind(int eventId, Class<? extends Event> eventClass) {
      this.eventId = (byte) eventId;
      this.eventClass = eventClass;
    }

    /**
     * Constructor for reuse of obsolete event kinds.
     * 
     * @param eventKind
     *          The event kind to reuse.
     */
    EventKind(EventKind eventKind) {
      this.eventId = eventKind.eventId;
      this.eventClass = eventKind.eventClass;
    }

    @Override
    public Byte identifier() {
      return eventId;
    }

    private static ReverseEnumMap<Byte, EventKind> map = new ReverseEnumMap<Byte, EventKind>(EventKind.class);

    /**
     * Converts the given ID of and event to appropriate {@link EventKind}
     * instance.
     * 
     * @param eventId
     *          The event ID to convert.
     * 
     * @throws InvalidEventTypeException
     *           If the event has no mapping.
     */
    @Override
    public EventKind convert(Byte eventId) throws InvalidEventTypeException {
      try {
        return map.get(eventId);
      } catch (IllegalArgumentException e) {
        throw new InvalidEventTypeException(eventId, e);
      }
    }

    /**
     * This is how this implementation checks whether debuggers use only correct
     * filters for particular event.
     * 
     * @param filter
     *          The filter to be checked whether it can modify this event.
     * @return True or false.
     */
    public boolean isFilterableBy(Filter<? extends Event> filter) {
      if (eventClass == null) {
        // Specification doesn't tell how to handle non
        // existent Events like CLASS_LOAD, FRAME_POP etc...
        throw new IllegalStateException("This event kind cannot be used! There is no specs for it! " + this);
      }
      return filter.getGenericClass().isAssignableFrom(eventClass);
    }
  }

  final static Logger logger = LoggerFactory.getLogger(EventBase.class);

  /**
   * Converts the events into to a single JDWP {@link EventCommand#COMPOSITE}
   * packet.
   * <p>
   * Reused from GNU Classpath Event.toPacket();
   * </p>
   * 
   * @param dos
   *          The stream to which to write data.
   * @param matchedEvents
   *          The events to send.
   * @param suspendPolicy
   *          The suspend policy enforced by the VM.
   * @returns a <code>JdwpPacket</code> of the events
   */
  public static JdwpPacket toPacket(DataOutputStream dos, List<? extends Event> matchedEvents, SuspendPolicy suspendPolicy) {
    JdwpPacket pkt;
    try {
      dos.writeByte(suspendPolicy.identifier());

      // we need to write the number of events at first hence we're writing the
      // events to a temporary stream
      ByteArrayOutputStream eventsOutputBytes = new ByteArrayOutputStream(0);
      DataOutputStream eventsOutputStream = new DataOutputStream(eventsOutputBytes);

      int events = 0;

      for (Event event : matchedEvents) {
        for (EventRequest<? extends Event> eventRequest : event.matchingEventRequests()) {
          logger.info(" >>>>>>>>> Sending event: {} for request: {}", event, eventRequest);
          event.write(eventsOutputStream, eventRequest.getId());
          events++;
        }
      }

      dos.writeInt(events);
      dos.write(eventsOutputBytes.toByteArray());

      pkt = new JdwpCommandPacket(CommandSet.EVENT, EventCommand.COMPOSITE);
    } catch (IOException ioe) {
      pkt = null;
    }

    return pkt;
  }

}
