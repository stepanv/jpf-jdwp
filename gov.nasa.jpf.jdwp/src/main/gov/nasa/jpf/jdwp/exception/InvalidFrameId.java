package gov.nasa.jpf.jdwp.exception;

public class InvalidFrameId extends JdwpError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6672423422452261216L;

	public InvalidFrameId(long frameId) {
		super(ErrorType.INVALID_FRAMEID, "Invalid frame ID: '" + frameId + "'");
	}

	public InvalidFrameId(Throwable cause) {
		super(ErrorType.INVALID_FRAMEID, cause);
	}

	public InvalidFrameId(long frameId, Throwable cause) {
		super(ErrorType.INVALID_FRAMEID, "Invalid frame ID: '" + frameId + "'", cause);
	}

}
