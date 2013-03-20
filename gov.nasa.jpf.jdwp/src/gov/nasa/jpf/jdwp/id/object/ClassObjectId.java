package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jvm.ElementInfo;

public class ClassObjectId extends ObjectId<ElementInfo> {

	public ClassObjectId(long id, ElementInfo object) {
		super(Tag.CLASS_OBJECT, id, object);
	}

}
