package gov.nasa.jpf.jdwp.id.object;

import java.util.ArrayList;

import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ElementInfo;

/**
 * This class implements the corresponding arrayID common data type from the
 * JDWP Specification.
 * 
 * <p>
 * <h2>JDWP Specification</h2>
 * Uniquely identifies an object in the target VM that is known to be an array.
 * </p>
 * 
 * @author stepan
 * 
 */
public class ArrayId extends ObjectId {

	/**
	 * Constructs the array ID.
	 * 
	 * @param id
	 *            The ID known by {@link ObjectIdManager}
	 * @param object
	 *            The {@link ElementInfo} instance that needs JDWP ID
	 *            representation.
	 */
	public ArrayId(long id, ElementInfo object) {
		super(Tag.ARRAY, id, object);
		new ArrayList<Object>();
	}

}
