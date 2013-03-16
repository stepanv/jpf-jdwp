package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.JdwpIdManager;
import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.id.ThreadId;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.jvm.LocalVarInfo;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum StackFrameCommand implements Command, IdentifiableEnum<Byte, StackFrameCommand> {
	GETVALUES(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError {
			ThreadId threadId = idManager.readThreadId(bytes);
			ThreadInfo thread = threadId.get();

			long frameID = bytes.getLong();
			StackFrame frame = VirtualMachineHelper.getFrame(thread, frameID);

			// throw new RuntimeException("not yet");
			int slots = bytes.getInt();
			os.writeInt(slots);

			for (int i = 0; i < slots; i++) {
				int slot = bytes.getInt();
				byte tag = bytes.get();

				Object object = null;
				for (LocalVarInfo localVarInfo : frame.getMethodInfo().getLocalVars()) {
					if (localVarInfo.getSlotIndex() == slot) {
						object = frame.getLocalValueObject(localVarInfo);
						break;
					}

				}

				Value value = Value.Tag.taggedObjectToValue(tag, object);
				value.writeTagged(os);
			}
		}
	},
	SETVALUES(2) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) {
		}
	},
	THISOBJECT(3) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) {
		}
	},
	POPFRAMES(4) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) {
		}
	};
	
	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public StackFrameCommand convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	private byte commandId;

	private StackFrameCommand(int commandId) {
		this.commandId = (byte)commandId;
	}
	
	
	private static ReverseEnumMap<Byte, StackFrameCommand> map = new ReverseEnumMap<Byte, StackFrameCommand>(StackFrameCommand.class);
	

}
