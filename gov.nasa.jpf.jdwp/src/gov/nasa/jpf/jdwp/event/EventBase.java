package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.command.ConvertibleEnum;
import gov.nasa.jpf.jdwp.command.ReverseEnumMap;
import gov.nasa.jpf.jdwp.event.filter.Filter;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.id.object.ThreadId;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class EventBase implements Event {

	private EventKind eventKind;

	public EventBase(EventKind eventKind, ThreadId threadId) {
		this.eventKind = eventKind;
		this.threadId = threadId;
	}

	public static enum EventKind implements ConvertibleEnum<Byte, EventKind> {
		/** Never sent across JDWP */
		VM_DISCONNECTED(100),

		SINGLE_STEP(1, SingleStepEvent.class), BREAKPOINT(2, BreakpointEvent.class), FRAME_POP(3), EXCEPTION(4, ExceptionEvent.class), USER_DEFINED(5), THREAD_START(
				6, ThreadStartEvent.class),

		/** JDWP.EventKind.THREAD_END */
		THREAD_DEATH(7, ThreadDeathEvent.class), CLASS_PREPARE(8, ClassPrepareEvent.class), CLASS_UNLOAD(9, ClassUnloadEvent.class), CLASS_LOAD(10), FIELD_ACCESS(
				20, FieldAccessEvent.class), FIELD_MODIFICATION(21, FieldModificationEvent.class), EXCEPTION_CATCH(30), METHOD_ENTRY(40, MethodEntryEvent.class), METHOD_EXIT(41,
				MethodExitEvent.class), METHOD_EXIT_WITH_RETURN_VALUE(42, MethodExitWithReturnValueEvent.class), MONITOR_CONTENDED_ENTER(43,
				MonitorContendedEnterEvent.class), MONITOR_CONTENDED_ENTERED(44, MonitorContendedEnteredEvent.class), MONITOR_WAIT(45, MonitorWaitEvent.class), MONITOR_WAITED(
				46, MonitorWaitedEvent.class),

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
				return true; // Specification doesn't tell how to handle non existent Events like (CLASS_LOAD)... TODO
			}
			return filter.getGenericClass().isAssignableFrom(eventClass);
		}
	}

	private ThreadId threadId;

	public ThreadId getThread() {
		return threadId;
	}

	@Override
	public EventKind getEventKind() {
		return eventKind;
	}

	protected abstract void writeSpecific(DataOutputStream os) throws IOException;

	@Override
	public void write(DataOutputStream os, int requestId) throws IOException {
		os.writeInt(requestId);
		threadId.write(os);
		writeSpecific(os);
	}
	

	@Override
	public String toString() {
		return "Event: " + super.toString() + ", kind: " + eventKind + ", thread: " + threadId;
	}

}