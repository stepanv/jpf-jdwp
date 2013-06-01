package gov.nasa.jpf.jdwp.event;

import java.io.DataOutputStream;
import java.io.IOException;

import gov.nasa.jpf.jdwp.event.EventBase.EventKind;

public interface Event {

	EventKind getEventKind();

	void write(DataOutputStream dos, int id) throws IOException;

}
