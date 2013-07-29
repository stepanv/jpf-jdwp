package gov.nasa.jpf.jdwp.exception;

import gov.nasa.jpf.jdwp.id.Identifier;

public class InvalidFieldId extends InvalidIdentifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2187774892944476187L;

	public InvalidFieldId(Identifier<?> identifier) {
		super(ErrorType.INVALID_FIELDID, identifier);
	}

}
