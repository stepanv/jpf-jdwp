package gov.nasa.jpf.jdwp.exception;

public class InvalidThreadException extends JdwpError {

	InvalidThreadException() {
		super(ErrorType.INVALID_THREAD);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 203403989030955960L;

}
