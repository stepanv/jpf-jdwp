package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.ExceptionEvent;
import gov.nasa.jpf.jdwp.event.ExceptionOnlyFilterable;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;

/**
 * <p>
 * Exception Only filter class that restricts reported exceptions.
 * </p>
 * <p>
 * Can be used with {@link ExceptionOnlyFilterable} events.
 * </p>
 * <p>
 * <h2>JDWP Specification</h2>
 * Restricts reported exceptions by their class and whether they are caught or
 * uncaught. This modifier can be used with exception event kinds only.
 * </p>
 * 
 * @see ExceptionEvent
 * @see ExceptionOnlyFilterable
 * 
 * @author stepan
 * 
 */
public class ExceptionOnlyFilter extends Filter<ExceptionOnlyFilterable> {
	ReferenceTypeId exceptionOrNull;
	boolean caught;
	boolean uncaught;

	/**
	 * 
	 * @param exceptionOrNull
	 *            Exception to report. Null (0) means report exceptions of all
	 *            types. A non-null type restricts the reported exception events
	 *            to exceptions of the given type or any of its subtypes.
	 * @param caught
	 *            Report caught exceptions
	 * @param uncaught
	 *            Report uncaught exceptions. Note that it is not always
	 *            possible to determine whether an exception is caught or
	 *            uncaught at the time it is thrown. See the exception event
	 *            catch location under composite events for more information.
	 */
	public ExceptionOnlyFilter(ReferenceTypeId exceptionOrNull, boolean caught, boolean uncaught) {
		super(ModKind.EXCEPTION_ONLY);
		this.exceptionOrNull = exceptionOrNull;
		this.caught = caught;
		this.uncaught = uncaught;
	}

	@Override
	public boolean matches(ExceptionOnlyFilterable event) {
		return event.visit(this);
	}

}
