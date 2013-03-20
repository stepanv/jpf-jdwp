package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.JdwpIdManager;
import gov.nasa.jpf.jdwp.exception.JdwpError;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum ArrayTypeCommandSet implements Command, IdentifiableEnum<Byte, ArrayTypeCommandSet> {
	NEWINSTANCE(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError {
			// TODO Auto-generated method stub

		}
	};
	private byte commandId;

	private ArrayTypeCommandSet(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, ArrayTypeCommandSet> map = new ReverseEnumMap<Byte, ArrayTypeCommandSet>(ArrayTypeCommandSet.class);

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public ArrayTypeCommandSet convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public abstract void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError;
}