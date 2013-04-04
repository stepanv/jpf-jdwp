package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.command.CommandContextProvider;
import gov.nasa.jpf.jdwp.command.ConvertibleEnum;
import gov.nasa.jpf.jdwp.command.ReverseEnumMap;
import gov.nasa.jpf.jdwp.event.Event.EventKind;
import gov.nasa.jpf.jdwp.event.filter.Filter;
import gov.nasa.jpf.jdwp.exception.JdwpError;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EventRequest  {

	public enum SuspendPolicy implements ConvertibleEnum<Byte, SuspendPolicy> {
		/** Suspend no threads when this event is encountered. */
		NONE(0),
		/** Suspend the event thread when this event is encountered. */
		EVENT_THREAD(1),
		/** Suspend all threads when this event is encountered. */
		ALL(2);

		SuspendPolicy(int suspendPolicyId) {
			this.suspendPolicyId = (byte) suspendPolicyId;
		}

		byte suspendPolicyId;

		@Override
		public Byte identifier() {
			return suspendPolicyId;
		}

		private static ReverseEnumMap<Byte, SuspendPolicy> map = new ReverseEnumMap<Byte, SuspendPolicy>(SuspendPolicy.class);

		@Override
		public SuspendPolicy convert(Byte val) throws JdwpError {
			return map.get(val);
		}
	}

	private SuspendPolicy suspendPolicy;

	private int id;
	
	private static AtomicInteger requestIdCounter = new AtomicInteger(1);
	
	public static EventRequest factory(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
		EventKind eventKind = EventKind.BREAKPOINT.convert(bytes.get());
		SuspendPolicy suspendPolicy = SuspendPolicy.ALL.convert(bytes.get());
		
		List<Filter<? extends IEvent>> filters = new ArrayList<Filter<? extends IEvent>>();
		
		int modifiers = bytes.getInt();
		for (int i = 0; i < modifiers; ++i) {
			filters.add(Filter.factory(bytes, contextProvider));
		}
		
		return new EventRequest(eventKind, suspendPolicy, filters);
	}

	public EventRequest(EventKind eventKind, SuspendPolicy suspendPolicy, List<Filter<? extends IEvent>> filters) {
		this(eventKind, suspendPolicy, filters, requestIdCounter.incrementAndGet());
	}
	
	private EventRequest(EventKind eventKind, SuspendPolicy suspendPolicy, List<Filter<? extends IEvent>> filters, int eventRequestId) {
		this.eventKind = eventKind;
		this.suspendPolicy = suspendPolicy;
		this.filters = filters;
		this.id = eventRequestId;
	}
	
	public static EventRequest nullRequestIdFactory(EventKind eventKind, SuspendPolicy suspendPolicy, List<Filter<? extends IEvent>> filters) {
		return new EventRequest(eventKind, suspendPolicy, filters, 0);
	}

	private List<Filter<? extends IEvent>> filters;
	private EventKind eventKind;

	/**
	 * <p>
	 * Event filters are applied in the same order as they were registered by
	 * the debugger.<br/>
	 * If filter doesn't match given event, no more filters are processed and
	 * this method return immediately.<br/>
	 * This is how count filter works even though it's not clear from the
	 * specification.
	 * </p>
	 * 
	 * @param event
	 *            The event to test against the request.
	 * @return Whether given event matches this request.
	 */
	public boolean matches(IEvent event) {
		if (filters == null) {
			return true;
		}
		for (Filter<? extends IEvent> filter : filters) {
			if (!filter.matches(event) ) {
				return false;
			}
		}

		return true;
	}

	public EventKind getEventKind() {
		return eventKind;
	}
	
	public SuspendPolicy getSuspendPolicy() {
		return suspendPolicy;
	}
	
	public int getId() {
		return id;
	}
	
	public String toString() {
		return String.format("Request ID %d, kind: %s", id, eventKind) ;
	}

}
