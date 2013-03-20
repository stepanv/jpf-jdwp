package gov.nasa.jpf.jdwp.variable;

import java.io.DataOutputStream;
import java.io.IOException;

public class CharValue extends PrimitiveValue {

	private char value;

	public CharValue(char value) {
		super(Tag.CHAR);
		this.value = value;
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		os.writeChar(value);
	}

}
