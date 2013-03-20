package gov.nasa.jpf.jdwp.variable;

import java.io.DataOutputStream;
import java.io.IOException;

public class ByteValue extends PrimitiveValue {

	private byte value;
	public ByteValue(byte value) {
		super(Value.Tag.BYTE);
		this.value = value;
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		os.writeByte(value);
		
	}

}
