package gov.nasa.jpf.jdwp.event;

import java.io.DataOutputStream;
import java.io.IOException;

public class ClassUnloadEvent extends Event {

	int requestId;
	public ClassUnloadEvent(int requestId, EventKind eventKind) {
		super(requestId, eventKind, null);
		this.requestId = requestId;
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Must be overridden because this event type, as an exception, doesn't have associated thread
	 */
	@Override
	public void write(DataOutputStream os) throws IOException {
		
		os.writeInt(requestId);
		threadId.write(os);
		
	}

	@Override
	protected void writeSpecific(DataOutputStream os) throws IOException {
		// TODO Auto-generated method stub

	}

}
