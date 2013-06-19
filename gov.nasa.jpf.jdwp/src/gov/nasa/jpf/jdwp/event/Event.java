package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.event.EventBase.EventKind;
import gov.nasa.jpf.jdwp.event.filter.Filter;

import java.io.DataOutputStream;
import java.io.IOException;

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

}
