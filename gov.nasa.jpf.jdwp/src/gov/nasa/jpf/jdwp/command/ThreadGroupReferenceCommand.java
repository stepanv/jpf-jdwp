package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum ThreadGroupReferenceCommand implements Command, ConvertibleEnum<Byte, ThreadGroupReferenceCommand> {
	NAME(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	PARENT(2) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	CHILDREN(3) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	};
	private byte commandId;

	private ThreadGroupReferenceCommand(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, ThreadGroupReferenceCommand> map = new ReverseEnumMap<Byte, ThreadGroupReferenceCommand>(
			ThreadGroupReferenceCommand.class);

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public ThreadGroupReferenceCommand convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public abstract void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;
}