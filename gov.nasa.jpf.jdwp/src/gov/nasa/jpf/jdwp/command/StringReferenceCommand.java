package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum StringReferenceCommand implements Command, ConvertibleEnum<Byte, StringReferenceCommand> {
	VALUE(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	};
	private byte commandId;

	private StringReferenceCommand(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, StringReferenceCommand> map = new ReverseEnumMap<Byte, StringReferenceCommand>(StringReferenceCommand.class);

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public StringReferenceCommand convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public abstract void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;
}