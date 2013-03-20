package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.JdwpIdManager;
import gov.nasa.jpf.jdwp.exception.JdwpError;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum EventRequestCommandSet implements Command, IdentifiableEnum<Byte, EventRequestCommandSet> {
	SET(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError {
			// TODO Auto-generated method stub

		}
	},
	CLEAR(2) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError {
			// TODO Auto-generated method stub

		}
	},
	CLEARALLBREAKPOINTS(3) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError {
			// TODO Auto-generated method stub

		}
	};
	private byte commandId;

	private EventRequestCommandSet(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, EventRequestCommandSet> map = new ReverseEnumMap<Byte, EventRequestCommandSet>(EventRequestCommandSet.class);

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public EventRequestCommandSet convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public abstract void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError;
}