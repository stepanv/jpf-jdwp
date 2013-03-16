package gov.nasa.jpf.jdwp.exception;

public class NotImplemented extends JdwpError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3022850035823199665L;

	NotImplemented() {
		super(ErrorType.NOT_IMPLEMENTED);
	}

}
