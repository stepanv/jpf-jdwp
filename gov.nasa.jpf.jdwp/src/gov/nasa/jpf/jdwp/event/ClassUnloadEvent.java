package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.variable.StringRaw;

import java.io.DataOutputStream;
import java.io.IOException;

public class ClassUnloadEvent extends Event {

	private String signature;

	public ClassUnloadEvent(String signature) {
		super(EventKind.CLASS_UNLOAD, null);
		this.signature = signature;
	}
	
	/**
	 * Must be overridden because this event type, as an exception, doesn't have associated thread
	 */
	@Override
	public void write(DataOutputStream os, int requestId) throws IOException {
		os.writeInt(requestId);
		new StringRaw(signature).write(os);
	}

	@Override
	protected void writeSpecific(DataOutputStream os) throws IOException {
	}

}
