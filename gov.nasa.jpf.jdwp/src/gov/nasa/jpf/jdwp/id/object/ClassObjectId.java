package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.variable.Value.Tag;
import gov.nasa.jpf.vm.ElementInfo;

public class ClassObjectId extends ObjectId<ElementInfo> {

	public ClassObjectId(long id, ElementInfo object) {
		super(Tag.CLASS_OBJECT, id, object);
	}

}
