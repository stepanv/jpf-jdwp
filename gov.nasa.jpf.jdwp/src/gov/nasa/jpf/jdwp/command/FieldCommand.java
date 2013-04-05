package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.JdwpError;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum FieldCommand implements Command, ConvertibleEnum<Byte, FieldCommand> {
	NONE;

	private static ReverseEnumMap<Byte, FieldCommand> map = new ReverseEnumMap<Byte, FieldCommand>(FieldCommand.class);

	@Override
	public Byte identifier() {
		return null;
	}

	@Override
	public FieldCommand convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
	}
}