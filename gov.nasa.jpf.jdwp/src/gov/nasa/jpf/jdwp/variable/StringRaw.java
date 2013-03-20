package gov.nasa.jpf.jdwp.variable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class StringRaw {
	String data;

	public StringRaw(String data) {
		this.data = data;
	}

	public void write(DataOutputStream os) throws IOException {
		byte[] utfEncoded = data.getBytes("UTF-8");
		os.writeInt(utfEncoded.length);
		os.write(utfEncoded);
	}

	public static String readString(ByteBuffer bytes) {
		int size = bytes.getInt();
		byte[] string = new byte[size];
		try {
			return new String(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// ALL VMs MUST implements UTF-8 (If compiled with 1.7, there is
			// already constant for this
			throw new RuntimeException("This VM doesn't conform to Java VM Specification!", e);
		}
	}

}
