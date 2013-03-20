package gov.nasa.jpf.jdwp.variable;

import java.io.DataOutputStream;
import java.io.IOException;

public class FloatValue extends PrimitiveValue {

	private float value;

	public FloatValue(float value) {
		super(Tag.FLOAT);
		this.value = value;
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		os.writeFloat(value);
	}

}
