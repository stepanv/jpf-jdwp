package gov.nasa.jpf.jdwp.command;

import gnu.classpath.jdwp.event.EventManager;
import gov.nasa.jpf.jdwp.event.EventBase.EventKind;
import gov.nasa.jpf.jdwp.event.EventRequest;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum EventRequestCommand implements Command, ConvertibleEnum<Byte, EventRequestCommand> {
	SET(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			EventRequest eventRequest = EventRequest.factory(bytes, contextProvider);

			EventManager.getDefault().requestEvent(eventRequest);
			contextProvider.getVirtualMachine().registerEventRequest(eventRequest);

			os.writeInt(eventRequest.getId());
		}
	},
	/**
	 * Clear an event request. See {@link EventKind} for a complete list of events
	 * that can be cleared. Only the event request matching the specified event
	 * kind and requestID is cleared. If there isn't a matching event request
	 * the command is a no-op and does not result in an error. Automatically
	 * generated events do not have a corresponding event request and may not be
	 * cleared using this command.
	 */
	CLEAR(2) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			 EventManager.getDefault().deleteRequest(bytes.get(), bytes.getInt());

		}
	},
	CLEARALLBREAKPOINTS(3) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	};
	private byte commandId;

	private EventRequestCommand(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, EventRequestCommand> map = new ReverseEnumMap<Byte, EventRequestCommand>(EventRequestCommand.class);

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public EventRequestCommand convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public abstract void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;
}