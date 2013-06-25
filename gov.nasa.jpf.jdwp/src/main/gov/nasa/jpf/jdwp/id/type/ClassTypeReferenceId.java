package gov.nasa.jpf.jdwp.id.type;

import gov.nasa.jpf.vm.ClassInfo;

public class ClassTypeReferenceId extends ReferenceTypeId {

	public ClassTypeReferenceId(long id, ClassInfo classInfo) {
		super(TypeTag.CLASS, id, classInfo);
	}

}