package gov.nasa.jpf.jdwp.id;

import gov.nasa.jpf.jdwp.exception.InvalidObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;

public class Identifier<T> {

	public static int SIZE = 8;
	private long id;
	private SoftReference<T> objectReference;

	public Identifier(long id, T object) {
		this.objectReference = new SoftReference<T>(object);
		this.id = id;
	}

	protected T object;
	
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
}
