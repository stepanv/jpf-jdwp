package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.JdwpIdManager;
import gov.nasa.jpf.jdwp.exception.JdwpError;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum FieldCommandSet implements Command, IdentifiableEnum<Byte, FieldCommandSet> {
	NONE;

	private static ReverseEnumMap<Byte, FieldCommandSet> map = new ReverseEnumMap<Byte, FieldCommandSet>(FieldCommandSet.class);

	@Override
	public Byte identifier() {
		return null;
	}

	@Override
	public FieldCommandSet convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError {
	}
}