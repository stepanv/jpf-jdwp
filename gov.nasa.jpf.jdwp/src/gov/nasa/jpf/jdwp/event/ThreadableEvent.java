package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.id.object.ThreadId;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class ThreadableEvent extends EventBase implements Event {

	public ThreadableEvent(EventKind eventKind, ThreadId threadId) {
		super(eventKind);
		this.threadId = threadId;
	}

	private ThreadId threadId;

	public ThreadId getThread() {
		return threadId;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", thread: " + threadId;
	}
	
	protected final void writeSpecific(DataOutputStream os) throws IOException {
		threadId.write(os);
		writeThreadableSpecific(os);
	}

	protected abstract void writeThreadableSpecific(DataOutputStream os) throws IOException;

}