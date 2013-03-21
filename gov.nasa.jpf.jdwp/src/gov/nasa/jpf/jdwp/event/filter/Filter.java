package gov.nasa.jpf.jdwp.event.filter;

import java.nio.ByteBuffer;

import gov.nasa.jpf.jdwp.command.CommandContextProvider;
import gov.nasa.jpf.jdwp.command.ConvertibleEnum;
import gov.nasa.jpf.jdwp.command.ReverseEnumMap;
import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.EventRequest;
import gov.nasa.jpf.jdwp.event.Event.EventKind;
import gov.nasa.jpf.jdwp.exception.IllegalArgumentException;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.object.ThreadId;

/**
 * 
 * Constraints used to control the number of generated events. Modifiers specify
 * additional tests that an event must satisfy before it is placed in the event
 * queue. Events are filtered by applying each modifier to an event in the order
 * they are specified in this collection Only events that satisfy all modifiers
 * are reported. A value of 0 means there are no modifiers in the request.
 * 
 * Filtering can improve debugger performance dramatically by reducing the
 * amount of event traffic sent from the target VM to the debugger VM.
 * 
 * @author stepan
 * 
 */
public abstract class Filter<T extends Event> {
	
	public enum ModKind implements ConvertibleEnum<Byte, ModKind> {
		COUNT(1) {
			@Override
			public Filter<?> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
				return new CountFilter(bytes.getInt());
			}
		},
		CONDITIONAL(2) {
			@Override
			public Filter<?> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
				// TODO Auto-generated method stub
				throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			}
		},
		THREAD_ONLY(3) {
			@Override
			public Filter<?> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
				ThreadId threadId = contextProvider.getObjectManager().readThreadId(bytes);
				return new ThreadOnlyFilter(threadId);
			}
		},
		CLASS_ONLY(4) {
			@Override
			public Filter<?> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
				// TODO Auto-generated method stub
				throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			}
		},
		CLASS_MATCH(5) {
			@Override
			public Filter<?> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
				// TODO Auto-generated method stub
				throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			}
		},
		CLASS_EXCLUDE(6) {
			@Override
			public Filter<?> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
				// TODO Auto-generated method stub
				throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			}
		},
		LOCATION_ONLY(7) {
			@Override
			public Filter<?> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
				// TODO Auto-generated method stub
				throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			}
		},
		EXCEPTION_ONLY(8) {
			@Override
			public Filter<?> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
				// TODO Auto-generated method stub
				throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			}
		},
		FIELD_ONLY(9) {
			@Override
			public Filter<?> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider)throws JdwpError {
				// TODO Auto-generated method stub
				throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			}
		},
		STEP(10) {
			@Override
			public Filter<?> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
				// TODO Auto-generated method stub
				throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			}
		},
		INSTANCE_ONLY(11) {
			@Override
			public Filter<?> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
				// TODO Auto-generated method stub
				throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			}
		},
		SOURCE_NAME_MATCH(12) {
			@Override
			public Filter<?> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
				// TODO Auto-generated method stub
				throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			}
		};
		
		private byte modKindId;

		ModKind(int modKindId) {
			this.modKindId = (byte) modKindId;
		}

		@Override
		public Byte identifier() {
			return modKindId;
		}

		ReverseEnumMap<Byte, ModKind> map = new ReverseEnumMap<Byte, Filter.ModKind>(ModKind.class);
		
		@Override
		public ModKind convert(Byte val) throws JdwpError {
			return map.get(val);
		}

		public abstract Filter<?> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError;
	}

	private ModKind modKind;
	
	public Filter(ModKind modKind) {
		this.modKind = modKind;
	}
	
	public static Filter<?> factory(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
		return ModKind.COUNT.convert(bytes.get()).createFilter(bytes, contextProvider);
	}

	protected abstract boolean matchesInternal(T event);
	
	final public boolean matches(T event) {
		if (!isAllowedEventKind(event.getEventKind())) {
			return false;
		}
		return matchesInternal(event);
	}

	public abstract boolean isAllowedEventKind(EventKind eventKind);
	
	public void addToEventRequest(EventRequest eventRequest) throws JdwpError {
		if (isAllowedEventKind(eventRequest.getEventKind())) {
			eventRequest.addFilter(this);
		} else 		{
			throw new IllegalArgumentException();
		}

	}

}
