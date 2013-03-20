package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jvm.ElementInfo;

public class ThreadGroupId extends ObjectId {

	public ThreadGroupId(long id, ElementInfo object) {
		super(Tag.THREAD_GROUP, id, object);
	}

}
