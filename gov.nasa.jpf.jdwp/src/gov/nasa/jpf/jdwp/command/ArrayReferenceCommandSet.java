package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.JdwpIdManager;
import gov.nasa.jpf.jdwp.exception.JdwpError;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum ArrayReferenceCommandSet implements Command, IdentifiableEnum<Byte, ArrayReferenceCommandSet> {
	LENGTH(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError {
			// TODO Auto-generated method stub
			
		}
	}, GETVALUES(2) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError {
			// TODO Auto-generated method stub
			
		}
	}, SETVALUES(3) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError {
			// TODO Auto-generated method stub
			
		}
	};

	private byte commandId;

	private ArrayReferenceCommandSet(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, ArrayReferenceCommandSet> map = new ReverseEnumMap<Byte, ArrayReferenceCommandSet>(ArrayReferenceCommandSet.class);

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public ArrayReferenceCommandSet convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public abstract void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError;
}