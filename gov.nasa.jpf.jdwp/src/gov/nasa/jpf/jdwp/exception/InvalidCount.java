package gov.nasa.jpf.jdwp.exception;


public class InvalidCount extends JdwpError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6057046391915954062L;

	public InvalidCount() {
		super(ErrorType.INVALID_COUNT);
	}

}
