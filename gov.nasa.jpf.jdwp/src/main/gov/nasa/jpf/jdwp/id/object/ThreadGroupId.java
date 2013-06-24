package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ElementInfo;

/**
 * This class implements the corresponding threadGroupID common data type from
 * the JDWP Specification.
 * 
 * <p>
 * <h2>JDWP Specification</h2>
 * Uniquely identifies an object in the target VM that is known to be a thread
 * group.
 * </p>
 * 
 * @author stepan
 * 
 */
public class ThreadGroupId extends ObjectId {

	/**
	 * Constructs the thread group ID.
	 * 
	 * @param id
	 *            The ID known by {@link ObjectIdManager}
	 * @param object
	 *            The {@link ElementInfo} instance that needs JDWP ID
	 *            representation.
	 */
	public ThreadGroupId(long id, ElementInfo object) {
		super(Tag.THREAD_GROUP, id, object);
	}

}
