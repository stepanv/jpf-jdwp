package gov.nasa.jpf.jdwp.event;

import gnu.classpath.jdwp.Jdwp;
import gov.nasa.jpf.jdwp.event.EventBase.EventKind;
import gov.nasa.jpf.jdwp.event.EventRequest.SuspendPolicy;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements management of event requests.
 * 
 * @author stepan
 * 
 */
public class EventRequestManager {
	final static Logger logger = LoggerFactory.getLogger(EventRequestManager.class);

	private EnumMap<EventKind, EventRequestTable<Event>> eventRequestMap = new EnumMap<EventKind, EventRequestTable<Event>>(EventKind.class);

	/**
	 * Creates Event Request Manager instance.
	 */
	public EventRequestManager() {

		loop: for (EventKind eventKind : EventKind.values()) {
			switch (eventKind) {

			// These two event kinds are only aliases to VM_START and
			// THREAD_DEATH (for compatibility reasons) according to the JDWP
			// Specification.
			case VM_INIT:
			case THREAD_END:
				break loop;

			// By default we want all event kinds to be registrable in this
			// event request manager.
			default:
				eventRequestMap.put(eventKind, new EventRequestTable<Event>());
			}

		}

		eventRequestMap.put(EventKind.VM_INIT, eventRequestMap.get(EventKind.VM_START));
		eventRequestMap.put(EventKind.THREAD_END, eventRequestMap.get(EventKind.THREAD_DEATH));

		registerSyntheticEventRequests();
	}

	/**
	 * This JDWP implementation uses {@link SuspendPolicy#ALL} for
	 * {@link EventKind#VM_INIT} event since this behavior is default in Oracle
	 * JVM.
	 * 
	 * <p>
	 * <h2>JPDA Specification</h2>
	 * Options for <tt>-agentlib:jdwp</tt>
	 * <table>
	 * <tr>
	 * <td>name</td>
	 * <td>required?</td>
	 * <td>default value</td>
	 * <td>description</td>
	 * </tr>
	 * <tr>
	 * <td>suspend</td>
	 * <td>no</td>
	 * <td>"y"</td>
	 * <td>If "y", VMStartEvent has a suspendPolicy of SUSPEND_ALL. If "n",
	 * VMStartEvent has a suspendPolicy of SUSPEND_NONE.</td>
	 * </tr>
	 * </table>
	 * </p>
	 * <p>
	 * <h2>JDWP Specification</h2>
	 * The VM Start Event and VM Death Event are automatically generated events.
	 * <br/>
	 * The value of the suspendPolicy field in the Event Data depends on the
	 * event. For the automatically generated VM Start Event the value of
	 * suspendPolicy is not defined and is therefore implementation or
	 * configuration specific. In the Sun implementation, for example, the
	 * suspendPolicy is specified as an option to the JDWP agent at
	 * launch-time.The automatically generated VM Death Event will have the
	 * suspendPolicy set to NONE.
	 * </p>
	 */
	public void registerSyntheticEventRequests() {
		/*
		 * According to the JDWP Specification, the VM Start Event and VM Death
		 * Event are automatically generated events.
		 */
		requestEvent(EventRequest.nullRequestIdFactory(EventKind.VM_INIT, Jdwp.suspendOnStartup() ? SuspendPolicy.ALL : SuspendPolicy.NONE, null));
		requestEvent(EventRequest.nullRequestIdFactory(EventKind.VM_DEATH, SuspendPolicy.NONE, null));
	}

	/**
	 * Removes the event request identified by the given ID of the given event
	 * kind.
	 * 
	 * @param eventKind
	 *            The event kind.
	 * @param requestId
	 *            The ID of the event request.
	 */
	public void removeEventRequest(EventKind eventKind, int requestId) {
		eventRequestMap.get(eventKind).deleteRequest(requestId);
	}

	/**
	 * Clears all event requests of the given event kind.
	 * 
	 * @param eventKind
	 *            The event kind.
	 */
	public void clearEventRequests(EventKind eventKind) {
		eventRequestMap.get(eventKind).clearRequests();
	}

	/**
	 * Returns the number of registered event requests for the given event kind.
	 * 
	 * @param eventKind
	 *            The event kind.
	 * @return The number of event requests .
	 */
	public int eventRequestCount(EventKind eventKind) {
		return eventRequestMap.get(eventKind).getEventRequestCount();
	}

	/**
	 * Registers the given event request in this event requests manager.
	 * 
	 * @param eventRequest
	 *            The event request to register.
	 */
	public void requestEvent(EventRequest<Event> eventRequest) {
		eventRequestMap.get(eventRequest.getEventKind()).requestEvent(eventRequest);
	}

	public SuspendPolicy populateMatchedEventsAndCalculateSuspension(Event event, List<Event> matchedEvents, SuspendPolicy resultSuspendPolicy) {
		return eventRequestMap.get(event.getEventKind()).populateMatchedEventsAndCalculateSuspension(event, matchedEvents, resultSuspendPolicy);
	}

	/**
	 * This class holds all event requests for a specific event kind.<br/>
	 * This table is thread safe.
	 * 
	 * @author stepan
	 * 
	 * @param <T>
	 *            Restricts the kind of event requests that are managed in this
	 *            table.
	 */
	private class EventRequestTable<T extends Event> {

		private Map<Integer, EventRequest<T>> requests = new HashMap<Integer, EventRequest<T>>();

		/**
		 * For the given event a matching requests (note that there might be
		 * multiple matching requests) are found. These matching requests are
		 * paired with the given event and inserted into the list of matching
		 * event requests.
		 * 
		 * @see EventRequest#addIfMatches(Event)
		 * @see SuspendPolicy#isLessRestrictiveThan(SuspendPolicy)
		 * 
		 * @param event
		 *            The event that is conditionally being notified.
		 * @param matchedEvents
		 *            The list of matched event that is being populated if a new
		 *            matching request is found.
		 * @param suspendPolicy
		 *            The incoming suspend policy that might be updated in a
		 *            return value.
		 * @return Updated suspend policy if it is more restrictive than the
		 *         incoming one
		 */
		public synchronized SuspendPolicy populateMatchedEventsAndCalculateSuspension(T event, List<Event> matchedEvents, SuspendPolicy suspendPolicy) {
			boolean eventMatched = false;

			// There might be more than one matching requests and thus loop
			// over all of them
			for (EventRequest<T> eventRequest : requests.values()) {
				if (event.addIfMatches(eventRequest)) {
					eventMatched = true;

					// update the suspend policy if the new one is more
					// restrictive than the current one
					if (suspendPolicy.isLessRestrictiveThan(eventRequest.getSuspendPolicy())) {
						suspendPolicy = eventRequest.getSuspendPolicy();
					}
				}
			}

			// the event matched to some event request and thus is eligible to
			// be sent across JDWP
			if (eventMatched) {
				matchedEvents.add(event);
			}

			return suspendPolicy;
		}

		/**
		 * Registers the given event request in this event kind table.
		 * 
		 * @param eventRequest
		 *            The event request to register.
		 */
		private synchronized void requestEvent(EventRequest<T> request) {
			requests.put(request.getId(), request);

			logger.info("Registered event request: {}", request);
		}

		/**
		 * Removes the event request identified by the given event request ID.
		 * 
		 * @param requestId
		 *            The ID of the event request.
		 */
		private synchronized void deleteRequest(int requestId) {
			EventRequest<T> request = requests.get(requestId);
			if (request != null) {
				requests.remove(requestId);
				logger.info("Removed event request: {}", request);
			}
		}

		/**
		 * Clears all event requests.
		 */
		private synchronized void clearRequests() {
			requests.clear();
		}

		/**
		 * Returns the number of registered event requests.
		 * 
		 * @return The number of event requests .
		 */
		private int getEventRequestCount() {
			return requests.size();
		}

	}

}
