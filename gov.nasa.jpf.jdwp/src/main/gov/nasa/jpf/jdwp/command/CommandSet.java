package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.JPF.Status;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum CommandSet implements ConvertibleEnum<Byte, CommandSet> {
	
	/**
	 * Virtual machine command set.
	 */
	VIRTUALMACHINE(1, VirtualMachineCommand.ALLCLASSES),
	
	/**
	 * Reference type command set.
	 */
	REFERENCETYPE(2, ReferenceTypeCommand.class),
	
	/**
	 * Class type command set.
	 */
	CLASSTYPE(3, ClassTypeCommand.INVOKEMETHOD),
	
	/**
	 * Array type command set.
	 */
	ARRAYTYPE(4, ArrayTypeCommand.NEWINSTANCE),
	
	/**
	 * Interface type command set.
	 */
	INTERFACETYPE(5, InterfaceTypeCommand.class),
	
	/**
	 * Method command set.
	 */
	METHOD(6, MethodCommand.BYTECODES),
	
	/**
	 * Field command set.
	 */
	FIELD(8, FieldCommand.class),
	
	/**
	 * Object reference command set.
	 */
	OBJECTREFERENCE(9, ObjectReferenceCommand.DISABLECOLLECTION),
	
	/**
	 * String reference command set.
	 */
	STRINGREFERENCE(10, StringReferenceCommand.VALUE),
	
	/**
	 * Thread reference command set.
	 */
	THREADREFERENCE(11, ThreadReferenceCommand.NAME),
	
	/**
	 * Thread group reference command set.
	 */
	THREADGROUPREFERENCE(12, ThreadGroupReferenceCommand.CHILDREN),
	
	/**
	 * Array reference command set.
	 */
	ARRAYREFERENCE(13, ArrayReferenceCommand.GETVALUES),
	
	/**
	 * Class loader reference command set.
	 */
	CLASSLOADERREFERENCE(14, ClassLoaderReferenceCommand.VISIBLECLASSES),
	
	/**
	 * Event request command set.
	 */
	EVENTREQUEST(15, EventRequestCommand.CLEAR),
	
	/**
	 * Stack frame command set.
	 */
	STACKFRAME(16, StackFrameCommand.GETVALUES),
	
	/**
	 * Class object reference command set.
	 */
	CLASSOBJECTREFERENCE(17, ClassObjectReferenceCommand.REFLECTEDTYPE),
	
	/**
	 * Event command set.
	 */
	EVENT(64, EventCommand.COMPOSITE);
	
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

	final static Logger logger = LoggerFactory.getLogger(CommandSet.class);

	public static void execute(Command command, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
		try {
			logger.debug("Running command: {} (class: {})", command, command.getClass());
			contextProvider.getVirtualMachine().getRunLock().lock();
			command.execute(bytes, os, contextProvider);
		} finally {
			contextProvider.getVirtualMachine().getRunLock().unlock();

			// This is how we detect JPF has terminated
			if (contextProvider.getJPF().getStatus() == Status.DONE) {
				// If JVM has terminated we don't care about any other
				// exceptions
				throw new JdwpError(ErrorType.VM_DEAD);
			}
		}
	}

}
