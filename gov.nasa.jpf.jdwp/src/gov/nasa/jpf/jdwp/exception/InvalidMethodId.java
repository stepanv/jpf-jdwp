package gov.nasa.jpf.jdwp.exception;

public class InvalidMethodId extends JdwpError {

	public InvalidMethodId(long id) {
		super(ErrorType.INVALID_METHODID);
	}

}
