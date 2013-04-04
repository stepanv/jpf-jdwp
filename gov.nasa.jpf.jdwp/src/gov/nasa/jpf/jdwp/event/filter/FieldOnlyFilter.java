package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.Event.EventKind;
import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;

/**
 * <h2>JDWP Spec:</h2> Restricts reported events to those that occur for a given
 * field. This modifier can be used with field access and field modification
 * event kinds only.
 * 
 * @author stepan
 * 
 */
public class FieldOnlyFilter extends Filter<Event> {

	ReferenceTypeId declaring;
	FieldId fieldId;

	public FieldOnlyFilter(ReferenceTypeId declaring, FieldId fieldId) {
		super(ModKind.FIELD_ONLY);
		this.declaring = declaring;
		this.fieldId = fieldId;
	}

	@Override
	public boolean matches(Event event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAllowedEventKind(EventKind eventKind) {
		switch (eventKind) {
		case FIELD_ACCESS:
		case FIELD_MODIFICATION:
			return true;
		default:
			return false;
		}
	}

}
