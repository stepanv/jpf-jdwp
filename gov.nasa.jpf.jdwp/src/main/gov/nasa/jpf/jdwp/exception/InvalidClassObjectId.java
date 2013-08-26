package gov.nasa.jpf.jdwp.exception;

import gov.nasa.jpf.jdwp.id.object.ClassObjectId;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Clazz is not the ID of a class.
 * </p>
 * 
 * Note that {@link ErrorType#INVALID_CLASS} is ambiguous since it's used even
 * for reference type as the JDWP Specification states.
 * 
 * @see InvalidReferenceType
 * 
 * @author stepan
 * 
 */
public class InvalidClassObjectId extends InvalidObject {

  /**
	 * 
	 */
  private static final long serialVersionUID = -4264642841819420585L;

  public InvalidClassObjectId(ErrorType errorType, ClassObjectId classObjectId) {
    super(ErrorType.INVALID_CLASS, classObjectId);
  }

}
