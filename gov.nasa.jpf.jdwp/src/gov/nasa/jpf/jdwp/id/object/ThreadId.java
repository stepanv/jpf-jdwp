package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jvm.ThreadInfo;

public class ThreadId extends ObjectId<ThreadInfo> {

	public ThreadId(long id, ThreadInfo object) {
		super(Tag.THREAD, id, object);
	}


}
