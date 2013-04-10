package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.command.ConvertibleEnum;
import gov.nasa.jpf.jdwp.command.ReverseEnumMap;
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

		SINGLE_STEP(1), BREAKPOINT(2), FRAME_POP(3), EXCEPTION(4), USER_DEFINED(5), THREAD_START(6),
		/** JDWP.EventKind.THREAD_END */
		THREAD_DEATH(7), CLASS_PREPARE(8), CLASS_UNLOAD(9), CLASS_LOAD(10), FIELD_ACCESS(20), FIELD_MODIFICATION(21), EXCEPTION_CATCH(30), METHOD_ENTRY(40), METHOD_EXIT(
				41), METHOD_EXIT_WITH_RETURN_VALUE(42), MONITOR_CONTENDED_ENTER(43), MONITOR_CONTENDED_ENTERED(44), MONITOR_WAIT(45), MONITOR_WAITED(46),
		/** JDWP.EventKind.VM_INIT */
		VM_START(90), VM_DEATH(99),

		/** obsolete - was used in jvmdi */
		VM_INIT(VM_START),
		/** obsolete - was used in jvmdi */
		THREAD_END(THREAD_DEATH);

		private byte eventId;

		EventKind(int eventId) {
			this.eventId = (byte) eventId;
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
	}

	private ThreadId threadId;

	public ThreadId getThread() {
		return threadId;
	}

	public EventKind getEventKind() {
		return eventKind;
	}

	protected abstract void writeSpecific(DataOutputStream os) throws IOException;

	public void write(DataOutputStream os, int requestId) throws IOException {
		os.writeInt(requestId);
		threadId.write(os);
		writeSpecific(os);
	}

}
