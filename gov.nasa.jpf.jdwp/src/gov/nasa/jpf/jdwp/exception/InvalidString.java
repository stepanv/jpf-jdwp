package gov.nasa.jpf.jdwp.exception;


/**
 * The string is invalid.
 * 
 * Thrown when incoming string doesn't meet constraints.
 * 
 * @author stepan
 * 
 */
public class InvalidString extends JdwpError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8157720539310443228L;

	/**
	 * Creates Invalid String exception.
	 * 
	 * @param invalidString
	 *            The string that is invalid.
	 */
	public InvalidString(String invalidString) {
		this(invalidString, null);
	}

	/**
	 * Creates Ivalid String exception.
	 * 
	 * @param invalidString
	 *            The string that is invalid.
	 * @param message
	 *            Additional message explaining why the string is invalid.
	 */
	public InvalidString(String invalidString, String message) {
		super(ErrorType.INVALID_STRING, "Invalid string: '" + invalidString + "'." + message != null ? " " + message : "");
	}

	/**
	 * Creates Ivalid String exception.
	 * 
	 * @param invalidString
	 *            The string that is invalid.
	 * @param message
	 *            Additional message explaining why the string is invalid.
	 * @param cause
	 *            The cause of this exception.
	 */
	public InvalidString(String invalidString, String message, Throwable cause) {
		super(ErrorType.INVALID_STRING, "Invalid string: '" + invalidString + "'." + message != null ? " " + message : "", cause);
	}

}
