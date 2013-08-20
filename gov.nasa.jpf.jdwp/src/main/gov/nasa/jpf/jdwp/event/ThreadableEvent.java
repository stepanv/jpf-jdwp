package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.exception.InvalidThreadException;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ThreadableEvent extends EventBase implements Event {

	final static Logger logger = LoggerFactory.getLogger(ThreadableEvent.class);
	
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
		logger.debug("Thread ID: {} .. for: {}",  threadId, threadInfo);
			try {
				if (threadId.get() == null || threadId.getInfoObject() != threadInfo) {
					throw new RuntimeException("Identifier for thread info instance: "+threadInfo+" is not valid.");
				}
			} catch (InvalidThreadException e) {
				throw new RuntimeException(e);
			}
		threadId.write(os);
		writeThreadableSpecific(os);
	}

	protected abstract void writeThreadableSpecific(DataOutputStream os) throws IOException;

}