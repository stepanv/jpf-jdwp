package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.JdwpIdManager;
import gov.nasa.jpf.jdwp.exception.JdwpError;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum StringReferenceCommandSet implements Command, IdentifiableEnum<Byte, StringReferenceCommandSet> {
	VALUE(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError {
			// TODO Auto-generated method stub

		}
	};
	private byte commandId;

	private StringReferenceCommandSet(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, StringReferenceCommandSet> map = new ReverseEnumMap<Byte, StringReferenceCommandSet>(StringReferenceCommandSet.class);

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public StringReferenceCommandSet convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public abstract void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError;
}