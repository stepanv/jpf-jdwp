package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jvm.ElementInfo;

public class ClassLoaderId extends ObjectId {

	public ClassLoaderId(long id, ElementInfo object) {
		super(Tag.CLASS_LOADER, id, object);
	}

}
