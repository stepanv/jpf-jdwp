package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.vm.VM;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum CommandSet implements ConvertibleEnum<Byte, CommandSet> {
	VIRTUALMACHINE(1, VirtualMachineCommand.ALLCLASSES), REFERENCETYPE(2, ReferenceTypeCommand.class), CLASSTYPE(3, ClassTypeCommand.INVOKEMETHOD), ARRAYTYPE(
			4, ArrayTypeCommand.NEWINSTANCE), INTERFACETYPE(5, InterfaceTypeCommand.class), METHOD(6, MethodCommand.BYTECODES), FIELD(8, FieldCommand.class), OBJECTREFERENCE(
			9, ObjectReferenceCommand.DISABLECOLLECTION), STRINGREFERENCE(10, StringReferenceCommand.VALUE), THREADREFERENCE(11, ThreadReferenceCommand.NAME), THREADGROUPREFERENCE(
			12, ThreadGroupReferenceCommand.CHILDREN), ARRAYREFERENCE(13, ArrayReferenceCommand.GETVALUES), CLASSLOADERREFERENCE(14,
			ClassLoaderReferenceCommand.VISIBLECLASSES), EVENTREQUEST(15, EventRequestCommand.CLEAR), STACKFRAME(16, StackFrameCommand.GETVALUES), CLASSOBJECTREFERENCE(
			17, ClassObjectReferenceCommand.REFLECTEDTYPE), EVENT(64, EventCommand.COMPOSITE);

	private static ReverseEnumMap<Byte, CommandSet> map = new ReverseEnumMap<Byte, CommandSet>(CommandSet.class);

	private byte commandSetId;

	private ConvertibleEnum<Byte, ? extends Command> commandConverterSample;

	public ConvertibleEnum<Byte, ? extends Command> getCommandConverterSample() {
		return commandConverterSample;
	}

	private CommandSet(int commandSetId, ConvertibleEnum<Byte, ? extends Command> commandConverterSample) {
		this.commandSetId = (byte) commandSetId;
		this.commandConverterSample = commandConverterSample;
	}

	private CommandSet(int commandSetId, Class<? extends ConvertibleEnum<Byte, ? extends Command>> commandConverterSample) {
		this.commandSetId = (byte) commandSetId;
		this.commandConverterSample = commandConverterSample.getEnumConstants()[0];
	}

	@Override
	public Byte identifier() {
		return commandSetId;
	}

	@Override
	public CommandSet convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	public static void execute(CommandSet commandSet, Command command, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
		
		System.out.println("Running: SET: " + commandSet + " (" + commandSet + "), CMD: " + command + " (" + command + ")");
		try {
			command.execute(bytes, os, contextProvider);
		} finally {
			if (VM.getVM().isEndState() && contextProvider.getVirtualMachine().isStarted()) {
				// If JVM has terminated we don't care about any other exceptions
				throw new JdwpError(ErrorType.VM_DEAD);
			}
		}
	}

}
