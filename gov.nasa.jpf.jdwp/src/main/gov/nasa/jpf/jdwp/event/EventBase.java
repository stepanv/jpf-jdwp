package gov.nasa.jpf.jdwp.event;

import gnu.classpath.jdwp.transport.JdwpCommandPacket;
import gnu.classpath.jdwp.transport.JdwpPacket;
import gov.nasa.jpf.jdwp.command.CommandSet;
import gov.nasa.jpf.jdwp.command.ConvertibleEnum;
import gov.nasa.jpf.jdwp.command.EventCommand;
import gov.nasa.jpf.jdwp.command.ReverseEnumMap;
import gov.nasa.jpf.jdwp.event.EventRequest.SuspendPolicy;
import gov.nasa.jpf.jdwp.event.filter.Filter;
import gov.nasa.jpf.jdwp.exception.JdwpError;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  protected abstract void writeSpecific(DataOutputStream os) throws IOException;

  @Override
  public String toString() {
    return "Event: " + super.toString() + ", kind: " + eventKind;
  }

  public static enum EventKind implements ConvertibleEnum<Byte, EventKind> {
    /** Never sent across JDWP */
    VM_DISCONNECTED(100),

    SINGLE_STEP(1, SingleStepEvent.class), BREAKPOINT(2, BreakpointEvent.class), FRAME_POP(3), EXCEPTION(4, ExceptionEvent.class), USER_DEFINED(
        5), THREAD_START(6, ThreadStartEvent.class),

    /** JDWP.EventKind.THREAD_END */
    THREAD_DEATH(7, ThreadDeathEvent.class), CLASS_PREPARE(8, ClassPrepareEvent.class), CLASS_UNLOAD(9, ClassUnloadEvent.class), CLASS_LOAD(
        10), FIELD_ACCESS(20, FieldAccessEvent.class), FIELD_MODIFICATION(21, FieldModificationEvent.class), EXCEPTION_CATCH(30), METHOD_ENTRY(
        40, MethodEntryEvent.class), METHOD_EXIT(41, MethodExitEvent.class), METHOD_EXIT_WITH_RETURN_VALUE(42,
        MethodExitWithReturnValueEvent.class), MONITOR_CONTENDED_ENTER(43, MonitorContendedEnterEvent.class), MONITOR_CONTENDED_ENTERED(44,
        MonitorContendedEnteredEvent.class), MONITOR_WAIT(45, MonitorWaitEvent.class), MONITOR_WAITED(46, MonitorWaitedEvent.class),

    /** JDWP.EventKind.VM_INIT */
    VM_START(90, VmStartEvent.class), VM_DEATH(99, VmDeathEvent.class),

    /** obsolete - was used in jvmdi */
    VM_INIT(VM_START),
    /** obsolete - was used in jvmdi */
    THREAD_END(THREAD_DEATH);

    private byte eventId;
    private Class<? extends Event> eventClass;

    EventKind(int eventId) {
      this.eventId = (byte) eventId;
      this.eventClass = null;
    }

    EventKind(int eventId, Class<? extends Event> eventClass) {
      this.eventId = (byte) eventId;
      this.eventClass = eventClass;
    }

    EventKind(EventKind eventKind) {
      this.eventId = eventKind.eventId;
    }

    @Override
    public Byte identifier() {
      return eventId;
    }

    private static ReverseEnumMap<Byte, EventKind> map = new ReverseEnumMap<Byte, EventKind>(EventKind.class);

    @Override
    public EventKind convert(Byte eventId) throws JdwpError {
      return map.get(eventId);
    }

    public boolean isFilterableBy(Filter<? extends Event> filter) {
      if (eventClass == null) {
        return true; // Specification doesn't tell how to handle non
        // existent Events like (CLASS_LOAD)... TODO
      }
      return filter.getGenericClass().isAssignableFrom(eventClass);
    }
  }

  final static Logger logger = LoggerFactory.getLogger(EventBase.class);

  /**
   * Converts the events into to a single JDWP {@link EventCommand#COMPOSITE}
   * packet
   * <p>
   * TODO Reused from GNU Classpath Event.toPacket();
   * </p>
   * 
   * @param dos
   *          the stream to which to write data
   * @param eventToRequestMap
   *          The events and their matching requests
   * @param suspendPolicy
   *          the suspend policy enforced by the VM
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
