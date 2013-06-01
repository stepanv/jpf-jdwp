package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ElementInfo;

public class ThreadGroupId extends ObjectId {

	public ThreadGroupId(long id, ElementInfo object) {
		super(Tag.THREAD_GROUP, id, object);
	}

}
