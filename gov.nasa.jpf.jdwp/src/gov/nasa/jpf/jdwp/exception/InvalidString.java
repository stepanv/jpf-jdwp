package gov.nasa.jpf.jdwp.exception;

public class InvalidString extends JdwpError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8157720539310443228L;

	InvalidString() {
		super(ErrorType.INVALID_STRING);
	}

}
