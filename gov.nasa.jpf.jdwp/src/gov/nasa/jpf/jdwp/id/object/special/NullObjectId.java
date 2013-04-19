package gov.nasa.jpf.jdwp.id.object.special;

import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.variable.Value.Tag;

/**
 * TODO Not implemented yet!
 * @author stepan
 *
 */
public class NullObjectId extends ObjectId<Object> {

	private static final Object NULL_OBJECT = new Object();
	
	private NullObjectId() {
		super(Tag.OBJECT, 0, NULL_OBJECT);
	}
	
	private static final NullObjectId instance = new NullObjectId();
	
	public static NullObjectId getInstance() {
		return instance;
	}

}
