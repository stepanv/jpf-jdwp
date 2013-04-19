package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.variable.Value.Tag;
import gov.nasa.jpf.vm.ElementInfo;

public class ArrayId extends ObjectId<ElementInfo> {

	public ArrayId(long id, ElementInfo object) {
		super(Tag.ARRAY, id, object);
	}

}
