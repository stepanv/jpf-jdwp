package gov.nasa.jpf.jdwp.id;

import java.io.DataOutputStream;
import java.io.IOException;

public class Identifier<T> {

	public static int SIZE = 8;
	private long id;

	public Identifier(long id, T object) {
		this.object = object;
		this.id = id;
	}

	protected T object;
	
	public T get() {
		return object;
	}
	
	public void write(DataOutputStream os) throws IOException {
		os.writeLong(id);
	}
}
