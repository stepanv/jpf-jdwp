package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum ClassTypeCommand implements Command, ConvertibleEnum<Byte, ClassTypeCommand> {
	SUPERCLASS(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	SETVALUES(2) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	INVOKEMETHOD(3) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	NEWINSTANCE(4) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	};
	private byte commandId;

	private ClassTypeCommand(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, ClassTypeCommand> map = new ReverseEnumMap<Byte, ClassTypeCommand>(ClassTypeCommand.class);

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public ClassTypeCommand convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public abstract void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;
}