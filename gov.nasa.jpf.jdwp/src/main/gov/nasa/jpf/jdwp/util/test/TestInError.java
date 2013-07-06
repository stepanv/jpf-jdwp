package gov.nasa.jpf.jdwp.util.test;

public class TestInError extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8624699896980355745L;

	public TestInError() {
	}

	public TestInError(String message) {
		super(message);
	}

	public TestInError(Throwable cause) {
		super(cause);
	}

	public TestInError(String message, Throwable cause) {
		super(message, cause);
	}

}
