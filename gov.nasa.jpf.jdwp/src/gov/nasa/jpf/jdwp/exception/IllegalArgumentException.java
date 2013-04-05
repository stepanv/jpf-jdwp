package gov.nasa.jpf.jdwp.exception;

public class IllegalArgumentException extends JdwpError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5106332507228617820L;

	public IllegalArgumentException() {
		super(ErrorType.ILLEGAL_ARGUMENT);
	}

}
