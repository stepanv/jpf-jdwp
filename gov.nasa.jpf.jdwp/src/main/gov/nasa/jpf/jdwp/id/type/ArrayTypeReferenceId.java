package gov.nasa.jpf.jdwp.id.type;

import gov.nasa.jpf.vm.ClassInfo;

/**
 * This class implements the corresponding <code>arrayTypeID</code> common data
 * type from the JDWP Specification. <br/>
 * These identifiers reference all the array types in the target program.
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

  /**
   * Array Type ID constructor.
   * 
   * @param id
   *          The numerical ID of this identifier.
   * @param classInfo
   *          The {@link ClassInfo} that stands for the desired array type.
   */
  public ArrayTypeReferenceId(long id, ClassInfo classInfo) {
    super(TypeTag.ARRAY, id, classInfo);
  }

}
