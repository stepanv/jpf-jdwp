package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.JdwpIdManager;
import gov.nasa.jpf.jdwp.exception.JdwpError;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


public enum CommandSet implements IdentifiableEnum<Byte, CommandSet> {
	VIRTUALMACHINE(1, VirtualMachineCommand.ALLCLASSES),
	REFERENCETYPE(2, ReferenceTypeCommandSet.class),
	CLASSTYPE(3, ClassTypeCommandSet.INVOKEMETHOD),
	ARRAYTYPE(4, ArrayTypeCommandSet.NEWINSTANCE),
	INTERFACETYPE(5, InterfaceTypeCommandSet.class),
	METHOD(6, MethodCommandSet.BYTECODES),
	FIELD(8, FieldCommandSet.class),
	OBJECTREFERENCE(9, ObjectReferenceCommandSet.DISABLECOLLECTION),
	STRINGREFERENCE(10, StringReferenceCommandSet.VALUE),
	THREADREFERENCE(11, ThreadReferenceCommandSet.NAME),
	THREADGROUPREFERENCE(12, ThreadGroupReferenceCommandSet.CHILDREN),
	ARRAYREFERENCE(13, ArrayReferenceCommandSet.GETVALUES),
	CLASSLOADERREFERENCE(14, ClassLoaderReferenceCommandSet.VISIBLECLASSES),
	EVENTREQUEST(15, EventRequestCommandSet.CLEAR),
	STACKFRAME(16, StackFrameCommand.GETVALUES),
	CLASSOBJECTREFERENCE(17, ClassObjectReferenceCommandSet.REFLECTEDTYPE),
	EVENT(64, EventCommandSet.COMPOSITE);

	private static ReverseEnumMap<Byte, CommandSet> map = new ReverseEnumMap<Byte, CommandSet>(CommandSet.class);

	private byte commandSetId;

	private IdentifiableEnum<Byte, ? extends Command> commandConverterSample;
	
	private CommandSet(int commandSetId, IdentifiableEnum<Byte, ? extends Command> commandConverterSample) {
		this.commandSetId = (byte) commandSetId;
		this.commandConverterSample = commandConverterSample;
	}
	
	private CommandSet(int commandSetId, Class<? extends IdentifiableEnum<Byte, ? extends Command>> commandConverterSample) {
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
	
	public static void execute(ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError {
		IdentifiableEnum<Byte, ? extends Command> commandConverterSample = map.get(bytes.get()).commandConverterSample;
		Command command = commandConverterSample.convert(bytes.get());
		command.execute(bytes, os, idManager);
	}
	public static void execute(byte commandSetId, byte commandId, ByteBuffer bytes, DataOutputStream os, JdwpIdManager idManager) throws IOException, JdwpError {
		System.out.println("Running: SET: " + commandSetId + ", CMD: " + commandId);
		IdentifiableEnum<Byte, ? extends Command> commandConverterSample = map.get(commandSetId).commandConverterSample;
		Command command = commandConverterSample.convert(commandId);
		command.execute(bytes, os, idManager);
	}
	
}
