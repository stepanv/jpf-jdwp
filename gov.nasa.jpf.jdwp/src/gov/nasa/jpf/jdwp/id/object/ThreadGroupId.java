package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.variable.Value.Tag;
import gov.nasa.jpf.vm.ElementInfo;

public class ThreadGroupId extends ObjectId<ElementInfo> {

	public ThreadGroupId(long id, ElementInfo object) {
		super(Tag.THREAD_GROUP, id, object);
	}

}
