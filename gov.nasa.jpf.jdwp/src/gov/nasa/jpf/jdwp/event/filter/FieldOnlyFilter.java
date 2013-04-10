package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.FieldOnlyFilterable;
import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;

/**
 * <p>
 * Can be used with {@link FieldOnlyFilterable} events.
 * </p>
 * <p>
 * <h2>JDWP Specification</h2>
 * Restricts reported events to those that occur for a given field. This
 * modifier can be used with field access and field modification event kinds
 * only.
 * </p>
 * TODO not done yet!
 * 
 * @author stepan
 * 
 */
public class FieldOnlyFilter extends Filter<FieldOnlyFilterable> {

	ReferenceTypeId declaring;
	FieldId fieldId;

	/**
	 * Creates Field Only filter.
	 * 
	 * @param declaring
	 *            Type in which field is declared.
	 * @param fieldId
	 *            Required field
	 */
	public FieldOnlyFilter(ReferenceTypeId declaring, FieldId fieldId) {
		super(ModKind.FIELD_ONLY, FieldOnlyFilterable.class);
		this.declaring = declaring;
		this.fieldId = fieldId;
	}

	@Override
	public boolean matches(FieldOnlyFilterable event) {
		// TODO Auto-generated method stub
		return false;
	}

}
