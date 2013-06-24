package gov.nasa.jpf.jdwp.exception;

public class InvalidLocation extends JdwpError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1139986632295562140L;

	InvalidLocation() {
		super(ErrorType.INVALID_LOCATION);
	}

}
