package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.command.CommandContextProvider;
import gov.nasa.jpf.jdwp.command.ConvertibleEnum;
import gov.nasa.jpf.jdwp.command.ReverseEnumMap;
import gov.nasa.jpf.jdwp.event.EventBase.EventKind;
import gov.nasa.jpf.jdwp.event.filter.Filter;
import gov.nasa.jpf.jdwp.exception.IllegalArgumentException;
import gov.nasa.jpf.jdwp.exception.JdwpError;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EventRequest<T extends Event> {

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

	public static <T extends Event> EventRequest<T> factory(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
		EventKind eventKind = EventKind.BREAKPOINT.convert(bytes.get());
		SuspendPolicy suspendPolicy = SuspendPolicy.ALL.convert(bytes.get());

		List<Filter<T>> filters = new ArrayList<Filter<T>>();

		int modifiers = bytes.getInt();
		for (int i = 0; i < modifiers; ++i) {
			Filter<T> filter = (Filter<T>) Filter.factory(bytes, contextProvider);

			if (!eventKind.isFilterableBy(filter)) {
				throw new IllegalArgumentException(); // TODO add arguments to the constructor: 'filter' is not allowed for 'eventKind'
			}

			filters.add((Filter<T>) filter);
			// TODO use this and throw illegal argument if class cast error
		}

		return new EventRequest<T>(eventKind, suspendPolicy, filters);
	}

	public EventRequest(EventKind eventKind, SuspendPolicy suspendPolicy, List<Filter<T>> filters) {
		this(eventKind, suspendPolicy, filters, requestIdCounter.incrementAndGet());
	}

	private EventRequest(EventKind eventKind, SuspendPolicy suspendPolicy, List<Filter<T>> filters, int eventRequestId) {
		this.eventKind = eventKind;
		this.suspendPolicy = suspendPolicy;
		this.filters = filters;
		this.id = eventRequestId;
	}

	public static <T extends Event> EventRequest<T> nullRequestIdFactory(EventKind eventKind, SuspendPolicy suspendPolicy, List<Filter<T>> filters) {
		return new EventRequest<T>(eventKind, suspendPolicy, filters, 0);
	}

	private List<Filter<T>> filters;
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
	public boolean matches(T event) {
		if (filters == null) {
			return true;
		}
		for (Filter<T> filter : filters) {
			if (!filter.matches(event)) {
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
		StringBuilder filters = new StringBuilder("Filters: ");
		if (this.filters != null) {
			for (Filter<? extends Event> filter : this.filters) {
				filters.append(filter.toString()).append(", ");
			}
		}
		return String.format("Request ID %d, kind: %s; >>> %s <<<", id, eventKind, filters);
	}

}
