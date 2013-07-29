package gov.nasa.jpf.jdwp.exception;

import gov.nasa.jpf.jdwp.id.FrameId;

public class InvalidFrameId extends InvalidIdentifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6672423422452261216L;

	public InvalidFrameId(FrameId frameId) {
		super(ErrorType.INVALID_FRAMEID, frameId);
	}

}
