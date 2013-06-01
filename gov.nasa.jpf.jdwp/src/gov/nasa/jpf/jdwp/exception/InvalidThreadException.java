package gov.nasa.jpf.jdwp.exception;

import gov.nasa.jpf.jdwp.id.object.ThreadId;

public class InvalidThreadException extends JdwpError {

	private ThreadId threadId; // TODO do something with this.

	public InvalidThreadException(ThreadId threadId) {
		super(ErrorType.INVALID_THREAD);
		this.threadId = threadId;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 203403989030955960L;

}
