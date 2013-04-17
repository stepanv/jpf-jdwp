package gov.nasa.jpf.jdwp.id.object;

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
