package gov.nasa.jpf.jdwp.exception;

import gov.nasa.jpf.jdwp.event.filter.CountFilter;

/**
 * The count is invalid.
 * 
 * This exception applies only for {@link CountFilter}.
 * 
 * @author stepan
 * 
 */
public class InvalidCount extends JdwpError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6057046391915954062L;

	/**
	 * Creates Invalid Count Exception.
	 * 
	 * @param count
	 *            The invalid count.
	 */
	public InvalidCount(int count) {
		super(ErrorType.INVALID_COUNT, "Invalid count: '" + count + "' provided.");
	}

}
