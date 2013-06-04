package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.vm.ThreadInfo;

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
		ThreadInfo threadInfo;
		try {
			threadInfo = threadId.getInfoObject();
			return super.toString() + ", thread: " + threadInfo + " (threadId: " + threadId + ")";
		} catch (InvalidObject e) {
			return super.toString() + ", threadId: " + threadId;
		}
		
	}
	
	protected final void writeSpecific(DataOutputStream os) throws IOException {
		threadId.write(os);
		writeThreadableSpecific(os);
	}

	protected abstract void writeThreadableSpecific(DataOutputStream os) throws IOException;

}