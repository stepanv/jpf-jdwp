package gov.nasa.jpf.jdwp.id.object;

/**
 * TODO Not implemented yet!
 * @author stepan
 *
 */
public class NullObjectId extends ObjectId {

	private NullObjectId() {
		super(Tag.OBJECT, 0, null);
		throw new RuntimeException("Not implemented yet!");
	}
	
	private static final NullObjectId instance = new NullObjectId();
	
	public static NullObjectId getInstance() {
		return instance;
	}

}
