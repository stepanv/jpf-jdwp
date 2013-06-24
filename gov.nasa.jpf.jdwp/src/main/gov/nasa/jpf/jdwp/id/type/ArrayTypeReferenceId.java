package gov.nasa.jpf.jdwp.id.type;

import gov.nasa.jpf.vm.ClassInfo;

public class ArrayTypeReferenceId extends ReferenceTypeId {

	public ArrayTypeReferenceId(long id, ClassInfo classInfo) {
		super(TypeTag.ARRAY, id, classInfo);
	}

}
