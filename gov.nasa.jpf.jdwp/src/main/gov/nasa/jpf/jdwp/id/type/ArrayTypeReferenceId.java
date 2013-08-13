package gov.nasa.jpf.jdwp.id.type;

import gov.nasa.jpf.vm.ClassInfo;

/**
 * This class implements the corresponding <code>arrayTypeID</code> common data
 * type from the JDWP Specification.
 * 
 * <p>
 * <h2>JDWP Specification:</h2>
 * Uniquely identifies a reference type in the target VM that is known to be an
 * array type.
 * </p>
 * 
 * @author stepan
 */
public class ArrayTypeReferenceId extends ReferenceTypeId {

	public ArrayTypeReferenceId(long id, ClassInfo classInfo) {
		super(TypeTag.ARRAY, id, classInfo);
	}

}
