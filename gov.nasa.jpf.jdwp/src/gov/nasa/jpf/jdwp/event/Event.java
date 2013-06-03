package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.event.EventBase.EventKind;

import java.io.DataOutputStream;
import java.io.IOException;

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
