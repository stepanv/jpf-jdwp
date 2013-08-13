package gov.nasa.jpf.jdwp.id.type;

import gov.nasa.jpf.vm.ClassInfo;

/**
 * This class implements the corresponding <code>interfaceID</code> common data
 * type from the JDWP Specification.<br/>
 * 
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

	/**
	 * Interface Type ID constructor.
	 * 
	 * @param id
	 *            The numerical ID of this identifier.
	 * @param classInfo
	 *            The {@link ClassInfo} that stands for the desired interface type.
	 */
	public InterfaceTypeReferenceId(long id, ClassInfo classInfo) {
		super(TypeTag.INTERFACE, id, classInfo);
	}

}
