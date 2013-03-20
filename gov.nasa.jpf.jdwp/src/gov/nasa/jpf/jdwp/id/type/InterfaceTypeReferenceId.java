package gov.nasa.jpf.jdwp.id.type;

import gov.nasa.jpf.jvm.ClassInfo;


public class InterfaceTypeReferenceId extends ReferenceTypeId {

	public InterfaceTypeReferenceId(long id, ClassInfo classInfo) {
		super(TypeTag.INTERFACE, id, classInfo);
	}

}
