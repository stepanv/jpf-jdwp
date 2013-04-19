package gov.nasa.jpf.jdwp.command;

import gnu.classpath.jdwp.VMVirtualMachine;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.value.StringRaw;
import gov.nasa.jpf.vm.ElementInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum ThreadGroupReferenceCommand implements Command, ConvertibleEnum<Byte, ThreadGroupReferenceCommand> {
	NAME(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ObjectId<ElementInfo> oid = contextProvider.getObjectManager().readSafeObjectId(bytes, ElementInfo.class);
		    ElementInfo group = oid.get();
		    int nameref = group.getReferenceField("name");
		    ElementInfo name = contextProvider.getVirtualMachine().getJpf().getVM().getHeap().get(nameref);
		    new StringRaw(name.asString()).write(os);
		}
	},
	PARENT(2) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			  ObjectId<ElementInfo> oid = contextProvider.getObjectManager().readSafeObjectId(bytes, ElementInfo.class);
			    ElementInfo group = oid.get();
			    int parentref = group.getReferenceField("parent");
			    ElementInfo parent = VMVirtualMachine.vm.getJpf().getVM().getHeap().get(parentref);
			    System.out.println("Thread group parent: " + parent);
			    
			    os.writeLong(0L); //TODO this is not finished

		}
	},
	CHILDREN(3) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	};
	private byte commandId;

	private ThreadGroupReferenceCommand(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, ThreadGroupReferenceCommand> map = new ReverseEnumMap<Byte, ThreadGroupReferenceCommand>(
			ThreadGroupReferenceCommand.class);

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public ThreadGroupReferenceCommand convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public abstract void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;
}