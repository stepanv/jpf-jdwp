package gov.nasa.jpf.jdwp.exception;

import gov.nasa.jpf.jdwp.id.Identifier;
import gov.nasa.jpf.jdwp.id.object.ObjectId;

public class InvalidObject extends JdwpError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3401121682523839373L;
	private Identifier<?> identifier;

	public InvalidObject(Identifier<?> identifier) {
		super(ErrorType.INVALID_OBJECT);
		this.identifier = identifier;
	}
	
	protected InvalidObject(ErrorType errorType, ObjectId objectId) {
		super(errorType);
		this.identifier = objectId;
	}


	public String toString() {
		return super.toString() + " (Identifier: " + identifier + ")";
	}
}
