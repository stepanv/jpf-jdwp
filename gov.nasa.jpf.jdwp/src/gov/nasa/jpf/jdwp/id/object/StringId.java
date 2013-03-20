package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jvm.ElementInfo;

public class StringId extends ObjectId<ElementInfo> {

	public StringId(long id, ElementInfo object) {
		super(Tag.STRING, id, object);
	}

}
