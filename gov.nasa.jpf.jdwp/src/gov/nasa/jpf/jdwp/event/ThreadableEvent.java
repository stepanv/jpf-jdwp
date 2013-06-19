package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class ThreadableEvent extends EventBase implements Event {

	public ThreadableEvent(EventKind eventKind, ThreadInfo threadInfo) {
		super(eventKind);
		this.threadInfo = threadInfo;
	}

	private ThreadInfo threadInfo;

	public ThreadInfo getThread() {
		return threadInfo;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", thread: " + threadInfo;
	}
	
	protected final void writeSpecific(DataOutputStream os) throws IOException {
		ThreadId threadId = JdwpObjectManager.getInstance().getThreadId(threadInfo);
		threadId.write(os);
		writeThreadableSpecific(os);
	}

	protected abstract void writeThreadableSpecific(DataOutputStream os) throws IOException;

}