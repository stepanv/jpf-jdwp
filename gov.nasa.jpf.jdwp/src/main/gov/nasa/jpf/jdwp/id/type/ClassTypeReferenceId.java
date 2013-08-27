package gov.nasa.jpf.jdwp.id.type;

import gov.nasa.jpf.vm.ClassInfo;

/**
 * This class implements the corresponding <code>classID</code> common data type
 * from the JDWP Specification.<br/>
 * These identifiers reference all the {@link Class} subtypes in the target
 * program.
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

	/**
	 * Class Type ID constructor.
	 * 
	 * @param id
	 *            The numerical ID of this identifier.
	 * @param classInfo
	 *            The {@link ClassInfo} that stands for the desired class type.
	 */
  public ClassTypeReferenceId(long id, ClassInfo classInfo) {
    super(TypeTag.CLASS, id, classInfo);
  }

}
