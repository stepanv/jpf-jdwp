package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum ClassLoaderReferenceCommand implements Command, ConvertibleEnum<Byte, ClassLoaderReferenceCommand> {
	VISIBLECLASSES(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	};
	private byte commandId;

	private ClassLoaderReferenceCommand(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, ClassLoaderReferenceCommand> map = new ReverseEnumMap<Byte, ClassLoaderReferenceCommand>(
			ClassLoaderReferenceCommand.class);

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public ClassLoaderReferenceCommand convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public abstract void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;
}