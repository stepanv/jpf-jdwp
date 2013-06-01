package gov.nasa.jpf.jdwp.id.object.special;

import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.StackFrame;

/**
 * TODO Not implemented yet!
 * @author stepan
 *
 */
public class NullObjectId extends ObjectId {
	
	private NullObjectId() {
		super(Tag.OBJECT, 0, null);
	}
	
	public static NullObjectId getInstance() {
		return instance;
	}
	
	private static final NullObjectId instance = new NullObjectId();
	
	@Override
	public void push(StackFrame frame) throws InvalidObject {
		frame.pushRef(-1);
	}

}
