package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.event.EventBase.EventKind;
import gov.nasa.jpf.jdwp.event.filter.Filter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Event hierarchy root.
 * 
 * With events, we want to use native JPF objects as long as possible, contrary
 * to the {@link Filter} facility. The reason is that an event can be created
 * and not been sent across JDWP (if it is filtered or a matching request is not
 * found). Not resolving the ID of JPF or SuT objects helps the performance.
 * 
 * 
 * @author stepan
 * 
 */
public interface Event {

	/**
	 * The event kind of this event.
	 * 
	 * @return The event kind.
	 */
	EventKind getEventKind();

	/**
	 * Writes the event to the provided output stream.
	 * 
	 * @param dos
	 *            The stream, where to write this event.
	 * @param requestId
	 *            The ID of the request this event is paired with. <br/>
	 *            It's important to note that one event can pair with multiple
	 *            requests.
	 * @throws IOException
	 */
	void write(DataOutputStream dos, int requestId) throws IOException;

	/**
	 * Queries the given event request whether it matches this event. If
	 * matched, the event request is added to the matching event requests.
	 * 
	 * @param eventRequest
	 *            The event request against whom this event will be matched.
	 * @return True if event request matched hence was added to the matching
	 *         event requests, otherwise False.
	 * 
	 * @see Event#matchingEventRequests()
	 */
	<T extends Event> boolean addIfMatches(EventRequest<T> eventRequest);

	/**
	 * Gets the matching event requests that where queried throw the
	 * {@link Event#addIfMatches(EventRequest)} mathod.
	 * 
	 * @return The list of matching event requests.
	 */
	List<EventRequest<? extends Event>> matchingEventRequests();

}
