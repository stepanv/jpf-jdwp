package gov.nasa.jpf.jdwp.id;

import gov.nasa.jpf.jdwp.exception.InvalidIdentifier;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;

public abstract class Identifier<T> {

	public static int SIZE = 8;
	private long id;
	private SoftReference<T> objectReference;

	/**
	 * This is here to keep the reference in case we don't want a garbage
	 * collection
	 * 
	 * TODO [for PJA] do I do it correctly? Maybe this is completely wrong and I
	 * need to tell JPF directly to not collect it
	 */
	@SuppressWarnings("unused")
	private T object;

	public Identifier(long id, T object) {
		this.objectReference = new SoftReference<T>(object);
		this.id = id;
	}

	public boolean isNull() {
		return objectReference.get() == null;
	}

	public abstract T nullObjectHandler() throws InvalidIdentifier;

	public T get() throws InvalidIdentifier {
		T object = objectReference.get();

		if (object == null) {
			return nullObjectHandler();
		}
		return object;
	}

	/**
	 * Writes identifier as is into the given stream. <br/>
	 * If subclasses want to write by default with additional information they
	 * must introduce new method with different signature.
	 * 
	 * @param os
	 *            The stream where to write the identifier.
	 * @throws IOException
	 *             If an I/O Error occurs.
	 */
	final public void write(DataOutputStream os) throws IOException {
		os.writeLong(id);
	}

	public String toString() {
		try {
			return super.toString() + ", reference: " + get() + ", id: " + id;
		} catch (InvalidIdentifier e) {
			return "invalid reference, id: " + id;
		}
	}
}
