package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.ClassOnlyFilterable;
import gov.nasa.jpf.jdwp.event.ClassPrepareEvent;
import gov.nasa.jpf.jdwp.event.Locatable;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jvm.ClassInfo;

/**
 * <p>
 * Can be used with {@link ClassOnlyFilterable} events.
 * </p>
 * <p>
 * <h2>JDWP Specification</h2>
 * For class prepare events, restricts the events generated by this request to
 * be the preparation of the given reference type and any subtypes.<br/>
 * For other events, restricts the events generated by this request to those
 * whose location is in the given reference type or any of its subtypes. An
 * event will be generated for any location in a reference type that can be
 * safely cast to the given reference type.<br/>
 * This modifier can be used with any event kind except class unload, thread
 * start, and thread end.
 * </p>
 * 
 * @see Locatable
 * @see ClassPrepareEvent
 * 
 * @author stepan
 * 
 */
public class ClassOnlyFilter extends Filter<ClassOnlyFilterable> {

	private ReferenceTypeId referenceTypeId;

	/**
	 * Creates Class Only filter.
	 * 
	 * @param clazz
	 *            Required class
	 */
	public ClassOnlyFilter(ReferenceTypeId clazz) {
		super(ModKind.CLASS_ONLY);
		this.referenceTypeId = clazz;
	}

	@Override
	public boolean matches(ClassOnlyFilterable event) {
		return event.matches(this);
	}

	public boolean matches(ClassInfo referenceType) {
		return referenceType.isInstanceOf(referenceTypeId.get());
	}

}
