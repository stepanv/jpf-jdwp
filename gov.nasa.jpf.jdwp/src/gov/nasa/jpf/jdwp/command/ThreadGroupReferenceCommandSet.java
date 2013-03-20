package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.JdwpIdManager;
import gov.nasa.jpf.jdwp.exception.JdwpError;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum ThreadGroupReferenceCommandSet implements Command, IdentifiableEnum<Byte, ThreadGroupReferenceCommandSet> {
	NAME(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError {
			// TODO Auto-generated method stub

		}
	},
	PARENT(2) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError {
			// TODO Auto-generated method stub

		}
	},
	CHILDREN(3) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError {
			// TODO Auto-generated method stub

		}
	};
	private byte commandId;

	private ThreadGroupReferenceCommandSet(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, ThreadGroupReferenceCommandSet> map = new ReverseEnumMap<Byte, ThreadGroupReferenceCommandSet>(
			ThreadGroupReferenceCommandSet.class);

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public ThreadGroupReferenceCommandSet convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public abstract void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError;
}