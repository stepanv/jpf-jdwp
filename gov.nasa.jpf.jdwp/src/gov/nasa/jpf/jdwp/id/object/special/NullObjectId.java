package gov.nasa.jpf.jdwp.id.object.special;

import java.io.DataOutputStream;
import java.io.IOException;

import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.StackFrame;

/**
 * A special object used to represent null in SuT.<br/>
 * Remark - We don't need NullObjectId to represent special case ObjectId
 * children since null doesn't represent Thread nor Classloader nor any other
 * meaningful object.
 * 
 * @author stepan
 * 
 */
public class NullObjectId extends ObjectId {

	private NullObjectId() {
		super(Tag.OBJECT, 0, -1);
	}

	public static NullObjectId getInstance() {
		return instance;
	}

	/**
	 * Helper method that writes {@link NullObjectId} instance to the stream.
	 * 
	 * @param os
	 *            Where to write the null object
	 * @throws IOException
	 *             If IO error occurs
	 */
	public static void instantWrite(DataOutputStream os) throws IOException {
		instance.write(os);
	}

	/**
	 * Helper method that writes {@link NullObjectId} tagged instance to the
	 * stream.
	 * 
	 * @param os
	 *            Where to write the null object
	 * @throws IOException
	 *             If IO error occurs
	 */
	public static void instanceWriteTagged(DataOutputStream os) throws IOException {
		instance.writeTagged(os);
	}

	private static final NullObjectId instance = new NullObjectId();

	@Override
	public void push(StackFrame frame) throws InvalidObject {
		frame.pushRef(-1);
	}

}
