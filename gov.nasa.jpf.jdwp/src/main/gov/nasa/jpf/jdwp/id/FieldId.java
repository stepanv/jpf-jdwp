package gov.nasa.jpf.jdwp.id;

import gov.nasa.jpf.jdwp.exception.InvalidFieldId;
import gov.nasa.jpf.jdwp.exception.InvalidIdentifier;
import gov.nasa.jpf.vm.FieldInfo;

public class FieldId extends Identifier<FieldInfo>{

	public FieldId(Long id, FieldInfo object) {
		super(id, object);
	}

	@Override
	public FieldInfo nullObjectHandler() throws InvalidIdentifier {
		throw new InvalidFieldId(this);
	}
	
	
}
