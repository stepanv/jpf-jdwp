package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum ObjectReferenceCommand implements Command, ConvertibleEnum<Byte, ObjectReferenceCommand> {
	REFERENCETYPE(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			 ObjectId oid = contextProvider.getObjectManager().readObjectId(bytes);
			    Object obj = oid.get();
			    
			    ClassInfo clazz = null;
			    if (obj instanceof ThreadInfo) {
			    	clazz = ((ThreadInfo)obj).getClassInfo();
			    } else if (obj instanceof StackFrame) {
			    	clazz = ((StackFrame)obj).getClassInfo();
			    } else if (obj instanceof ElementInfo) {
			    	clazz = ((ElementInfo)obj).getClassInfo();
			    } else {
			    	throw new RuntimeException("object: ." + obj + "'(class: " + obj.getClass() + ") needs an reference type implementation"); //TODO complete the implementation
			    }
			    //throw new RuntimeException("not implemented");
			    ReferenceTypeId refId = contextProvider.getObjectManager().getReferenceTypeId(clazz);
			    refId.writeTagged(os);
		}
	},
	GETVALUES(2) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	SETVALUES(3) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	MONITORINFO(5) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	INVOKEMETHOD(6) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	DISABLECOLLECTION(7) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	ENABLECOLLECTION(8) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	ISCOLLECTED(9) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ObjectId oid = contextProvider.getObjectManager().readObjectId(bytes);
		    boolean collected = oid.get () == null; // TODO finish this
		    os.writeBoolean(collected);

		}
	};
	private byte commandId;

	private ObjectReferenceCommand(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, ObjectReferenceCommand> map = new ReverseEnumMap<Byte, ObjectReferenceCommand>(ObjectReferenceCommand.class);

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public ObjectReferenceCommand convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public abstract void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;
}