package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.id.object.ThreadId;

import java.io.DataOutputStream;
import java.io.IOException;

public class ThreadDeathEvent extends Event implements Threadable {

	public ThreadDeathEvent(ThreadId threadId) {
		super(EventKind.THREAD_DEATH, threadId);
	}

	@Override
	protected void writeSpecific(DataOutputStream os) throws IOException {
	}

}
