package gov.nasa.jpf.jdwp.exception;

public class InvalidObject extends JdwpError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3401121682523839373L;

	public InvalidObject() {
		super(ErrorType.INVALID_OBJECT);
	}

	public InvalidObject(String message, Throwable cause) {
		super(ErrorType.INVALID_OBJECT, message, cause);
	}

	public InvalidObject(String message) {
		super(ErrorType.INVALID_OBJECT, message);
	}

	public InvalidObject(Throwable cause) {
		super(ErrorType.INVALID_OBJECT, cause);
	}

}
