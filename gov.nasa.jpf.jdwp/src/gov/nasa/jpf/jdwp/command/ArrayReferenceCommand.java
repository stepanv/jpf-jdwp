package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum ArrayReferenceCommand implements Command, ConvertibleEnum<Byte, ArrayReferenceCommand> {
	LENGTH(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			
		}
	}, GETVALUES(2) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			
		}
	}, SETVALUES(3) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			
		}
	};

	private byte commandId;

	private ArrayReferenceCommand(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, ArrayReferenceCommand> map = new ReverseEnumMap<Byte, ArrayReferenceCommand>(ArrayReferenceCommand.class);

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public ArrayReferenceCommand convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public abstract void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;
}