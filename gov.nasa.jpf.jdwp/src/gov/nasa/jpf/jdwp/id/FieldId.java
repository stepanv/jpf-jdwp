package gov.nasa.jpf.jdwp.id;

import gov.nasa.jpf.jvm.FieldInfo;

public class FieldId extends Identifier<FieldInfo>{

	public FieldId(FieldInfo object) {
		super(0, object);
		throw new RuntimeException("NOT IMPLEMENTED YET: " + object);
	}

}
