package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.ExceptionEvent;
import gov.nasa.jpf.jdwp.event.ExceptionOnlyFilterable;
import gov.nasa.jpf.jdwp.exception.InvalidIdentifier;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;

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
		super(ModKind.EXCEPTION_ONLY, ExceptionOnlyFilterable.class);
		this.exceptionOrNull = exceptionOrNull;
		this.caught = caught;
		this.uncaught = uncaught;
	}

	@Override
	public boolean matches(ExceptionOnlyFilterable event) {
		// TODO need to finally decide whether nulls are allowed for Identifier instances - for ObjectIds we always get NullObjectId but this doesn't work for any Identifier (like here for ReferenceTypeId) 
		if (exceptionOrNull == null || exceptionOrNull.isNull()) {
			return true;
		}
		return event.visit(this);
	}

	public boolean matches(ExceptionEvent event) {

		// we don't want caught exceptions
		if (event.isCaught() && !caught) {
			return false;
		}

		// we don't want uncaught exceptions
		if (!event.isCaught() && !uncaught) {
			return false;
		}

		if (exceptionOrNull == null) {
			// return exception of all types as the specs states
			// it's not clear though whether we're able to receive null object
			// for ReferenceTypeId
			// TODO test null object here
			return true;
		}

		ElementInfo exception = event.getException();
		ClassInfo exceptionClassInfo = exception.getClassInfo();

		try {
			// restricts the reported exception events to exceptions of the
			// given type or any of its !!subtypes!!
			return exceptionClassInfo.isInstanceOf(exceptionOrNull.get());
		} catch (InvalidIdentifier e) {
			// exceptionOrNull stands for not known (GCed) type hence is ineffective
			return false;
		}
	}

}
