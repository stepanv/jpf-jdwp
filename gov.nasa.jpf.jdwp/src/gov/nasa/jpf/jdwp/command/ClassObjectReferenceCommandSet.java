package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.JdwpIdManager;
import gov.nasa.jpf.jdwp.exception.JdwpError;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum ClassObjectReferenceCommandSet implements Command, IdentifiableEnum<Byte, ClassObjectReferenceCommandSet> {
	REFLECTEDTYPE(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError {
			// TODO Auto-generated method stub

		}
	};
	private byte commandId;

	private ClassObjectReferenceCommandSet(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, ClassObjectReferenceCommandSet> map = new ReverseEnumMap<Byte, ClassObjectReferenceCommandSet>(
			ClassObjectReferenceCommandSet.class);

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public ClassObjectReferenceCommandSet convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public abstract void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError;
}