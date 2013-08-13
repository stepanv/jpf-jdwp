package gov.nasa.jpf.jdwp.id.type;

import gov.nasa.jpf.vm.ClassInfo;

/**
 * This class implements the corresponding <code>interfaceID</code> common data
 * type from the JDWP Specification.
 * 
 * <p>
 * <h2>JDWP Specification:</h2>
 * Uniquely identifies a reference type in the target VM that is known to be an
 * interface type.
 * </p>
 * 
 * @author stepan
 */
public class InterfaceTypeReferenceId extends ReferenceTypeId {

	public InterfaceTypeReferenceId(long id, ClassInfo classInfo) {
		super(TypeTag.INTERFACE, id, classInfo);
	}

}
