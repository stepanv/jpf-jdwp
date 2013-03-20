package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jvm.ThreadInfo;

public class ThreadStartEvent extends Event {

	private ThreadInfo threadInfo;

	public ThreadStartEvent(ThreadInfo currentThread) {
		super(EventKind.THREAD_START);
		this.threadInfo = currentThread;
	}

}
