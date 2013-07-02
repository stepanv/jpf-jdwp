package gov.nasa.jpf.jdwp.exception;

import gov.nasa.jpf.jdwp.id.object.ThreadId;

public class InvalidThreadException extends InvalidObject {

	public InvalidThreadException(ThreadId threadId) {
		super(ErrorType.INVALID_THREAD, threadId);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 203403989030955960L;

}
