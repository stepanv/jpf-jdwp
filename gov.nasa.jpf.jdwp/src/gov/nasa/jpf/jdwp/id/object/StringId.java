package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ElementInfo;

public class StringId extends ObjectId {

	public StringId(long id, ElementInfo object) {
		super(Tag.STRING, id, object);
	}

}
