package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.JdwpIdManager;
import gov.nasa.jpf.jdwp.exception.JdwpError;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


public enum CommandSet implements IdentifiableEnum<Byte, CommandSet> {
	VIRTUALMACHINE(1, VirtualMachineCommand.class),
	STACKFRAME(16, StackFrameCommand.GETVALUES);

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
