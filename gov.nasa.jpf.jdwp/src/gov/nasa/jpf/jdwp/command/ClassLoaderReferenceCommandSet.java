package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.JdwpIdManager;
import gov.nasa.jpf.jdwp.exception.JdwpError;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum ClassLoaderReferenceCommandSet implements Command, IdentifiableEnum<Byte, ClassLoaderReferenceCommandSet> {
	VISIBLECLASSES(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError {
			// TODO Auto-generated method stub

		}
	};
	private byte commandId;

	private ClassLoaderReferenceCommandSet(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, ClassLoaderReferenceCommandSet> map = new ReverseEnumMap<Byte, ClassLoaderReferenceCommandSet>(
			ClassLoaderReferenceCommandSet.class);

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public ClassLoaderReferenceCommandSet convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public abstract void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError;
}