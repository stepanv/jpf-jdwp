package gov.nasa.jpf.jdwp.id;

import gov.nasa.jpf.jdwp.exception.InvalidObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;

public class Identifier<T> {

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

	public void disableCollection() throws InvalidObject {
		object = get();
	}

	public void enableCollection() throws InvalidObject {
		object = null;
	}

	public T get() throws InvalidObject {
		T object = objectReference.get();

		if (object == null) {
			throw new InvalidObject();
		}
		return object;
	}

	public void write(DataOutputStream os) throws IOException {
		os.writeLong(id);
	}
	
	public String toString() {
		try {
			return super.toString() + ", reference: " + get() + ", id: " + id;
		} catch (InvalidObject e) {
			return "invalid reference, id: " + id;
		}
	}
}
