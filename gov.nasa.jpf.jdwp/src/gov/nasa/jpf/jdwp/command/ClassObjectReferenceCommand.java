package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum ClassObjectReferenceCommand implements Command, ConvertibleEnum<Byte, ClassObjectReferenceCommand> {
	REFLECTEDTYPE(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ObjectId<?> oid = contextProvider.getObjectManager().readObjectId(bytes); 
		    Object object = oid.get();
		    
		    ClassInfo ci = null;
		    if (object instanceof ClassInfo) {
		    	ci = (ClassInfo) object;
		    } else if (object instanceof ElementInfo) {
		    	ci = ((ElementInfo) object).getClassInfo();
		    } else {
		    	throw new RuntimeException("not implemented for object: " + object);
		    }
		    
		    // The difference between a ClassObjectId and a ReferenceTypeId is one is
		    // stored as an ObjectId and the other as a ReferenceTypeId.
		    ReferenceTypeId refId = contextProvider.getObjectManager().getReferenceTypeId(ci);
		    refId.writeTagged(os);

		}
	};
	private byte commandId;

	private ClassObjectReferenceCommand(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, ClassObjectReferenceCommand> map = new ReverseEnumMap<Byte, ClassObjectReferenceCommand>(
			ClassObjectReferenceCommand.class);

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public ClassObjectReferenceCommand convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public abstract void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;
}