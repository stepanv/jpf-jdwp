package gov.nasa.jpf.jdwp.exception;

public class InvalidMethodId extends JdwpError {

	InvalidMethodId() {
		super(ErrorType.INVALID_METHODID);
	}

}
