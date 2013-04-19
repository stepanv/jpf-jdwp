package gov.nasa.jpf.jdwp.variable;

import gov.nasa.jpf.jdwp.id.object.ObjectId;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>
 * Object Value class is a {@link Value} wrapper for all objects that have
 * {@link ObjectId}.
 * </p>
 * <p>
 * For JDWP Specification refer to {@link Value};
 * </p>
 * 
 * @author stepan
 * 
 */
public class ObjectValue<T extends ObjectId<?>> extends Value {

	private T objectId;

	public ObjectValue(T objectId) {
		super(objectId.getIdentifier());
		this.objectId = objectId;
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		objectId.write(os);
	}

}
