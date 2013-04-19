package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum StackFrameCommand implements Command, ConvertibleEnum<Byte, StackFrameCommand> {
	GETVALUES(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ThreadId threadId = contextProvider.getObjectManager().readThreadId(bytes);
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

				Value value = Tag.taggedObjectToValue(tag, object);
				value.writeTagged(os);
			}
		}
	},
	SETVALUES(2) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) {
		}
	},
	THISOBJECT(3) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) {
		}
	},
	POPFRAMES(4) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) {
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
