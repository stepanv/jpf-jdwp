package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.InstanceOnlyFilterable;
import gov.nasa.jpf.jdwp.event.Event.EventKind;
import gov.nasa.jpf.jdwp.event.IEvent;
import gov.nasa.jpf.jdwp.id.object.ObjectId;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Restricts reported events to those whose active 'this' object is the given
 * object. Match value is the null object for static methods. This modifier can
 * be used with any event kind except class prepare, class unload, thread start,
 * and thread end. Introduced in JDWP version 1.4.
 * </p>
 * 
 * @author stepan
 * 
 */
public class InstanceOnlyFilter extends Filter<InstanceOnlyFilterable> {

	private ObjectId<?> objectId;

	public InstanceOnlyFilter(ObjectId<?> objectId) {
		super(ModKind.INSTANCE_ONLY);
		this.objectId = objectId;
	}

	@Override
	public boolean matches(InstanceOnlyFilterable event) {
		// TODO Auto-generated method stub
		return false;
	}

}
