package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.Event.EventKind;
import gov.nasa.jpf.jdwp.event.ExceptionEvent;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;

public class ExceptionOnlyFilter extends Filter<ExceptionEvent> {
	ReferenceTypeId exceptionOrNull;
	boolean caught;
	boolean uncaught;

	public ExceptionOnlyFilter(ReferenceTypeId exceptionOrNull, boolean caught, boolean uncaught) {
		super(ModKind.EXCEPTION_ONLY);
		this.exceptionOrNull = exceptionOrNull;
		this.caught = caught;
		this.uncaught = uncaught;
	}

	@Override
	public boolean matches(ExceptionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	/** 
	 * <p>JDWP Spec is vague with this:<br/>
	 * <em>This modifier can be used with exception event kinds only.</em></p>
	 * <p> TODO needs to be investigated from other JDWP implementations </p>
	 *  
	 */
	@Override
	public boolean isAllowedEventKind(EventKind eventKind) {
		switch (eventKind) {
		case EXCEPTION_CATCH:
		case EXCEPTION:
			return true;
		default:
			return false;
		}
	}

}
