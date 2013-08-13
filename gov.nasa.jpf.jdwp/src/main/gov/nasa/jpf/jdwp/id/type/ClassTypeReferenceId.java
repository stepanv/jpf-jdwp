package gov.nasa.jpf.jdwp.id.type;

import gov.nasa.jpf.vm.ClassInfo;

/**
 * This class implements the corresponding <code>classID</code> common data type
 * from the JDWP Specification.
 * 
 * <p>
 * <h2>JDWP Specification:</h2>
 * Uniquely identifies a reference type in the target VM that is known to be a
 * class type.
 * </p>
 * 
 * @author stepan
 */
public class ClassTypeReferenceId extends ReferenceTypeId {

	public ClassTypeReferenceId(long id, ClassInfo classInfo) {
		super(TypeTag.CLASS, id, classInfo);
	}

}
