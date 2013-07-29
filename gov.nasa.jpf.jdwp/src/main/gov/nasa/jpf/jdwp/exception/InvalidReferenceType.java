package gov.nasa.jpf.jdwp.exception;

import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * RefType is not the ID of a reference type.
 * </p>
 * 
 * Note that {@link ErrorType#INVALID_CLASS} is ambiguous since it's used even
 * for reference type as the JDWP Specification states.
 * 
 * @see InvalidReferenceType
 * @author stepan
 * 
 */
public class InvalidReferenceType extends InvalidIdentifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5445794498004684701L;

	public InvalidReferenceType(ReferenceTypeId referenceTypeId) {
		super(ErrorType.INVALID_CLASS, referenceTypeId);
	}

}
