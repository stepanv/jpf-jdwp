package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.command.CommandContextProvider;
import gov.nasa.jpf.jdwp.command.ConvertibleEnum;
import gov.nasa.jpf.jdwp.command.ReverseEnumMap;
import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.Event.EventKind;
import gov.nasa.jpf.jdwp.event.IEvent;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.jdwp.variable.StringRaw;

import java.nio.ByteBuffer;

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
public abstract class Filter<T extends IEvent> {

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
				ReferenceTypeId referenceTypeId = contextProvider.getObjectManager().readReferenceTypeId(bytes);
				return new ClassOnlyFilter(referenceTypeId);
			}
		},
		CLASS_MATCH(5) {
			@Override
			public Filter<?> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
				String classPattern = StringRaw.readString(bytes);
				return new ClassMatchFilter(classPattern);
			}
		},
		CLASS_EXCLUDE(6) {
			@Override
			public Filter<?> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
				String classPattern = StringRaw.readString(bytes);
				return new ClassExcludeFilter(classPattern);
			}
		},
		LOCATION_ONLY(7) {
			@Override
			public Filter<?> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
				Location location = Location.factory(bytes, contextProvider);
				return new LocationOnlyFilter(location);
			}
		},
		EXCEPTION_ONLY(8) {
			@Override
			public Filter<?> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
				ReferenceTypeId exceptionOrNull = contextProvider.getObjectManager().readReferenceTypeId(bytes);
				boolean uncaught = bytes.get() != 0;
				boolean caught = bytes.get() != 0;
				return new ExceptionOnlyFilter(exceptionOrNull, caught, uncaught);
			}
		},
		FIELD_ONLY(9) {
			@Override
			public Filter<?> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
				ReferenceTypeId declaring = contextProvider.getObjectManager().readReferenceTypeId(bytes);
				FieldId fieldId = FieldId.factory(bytes, contextProvider);
				return new FieldOnlyFilter(declaring, fieldId);
			}
		},
		STEP(10) {
			@Override
			public Filter<?> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
				return StepFilter.factory(bytes, contextProvider);
			}
		},
		INSTANCE_ONLY(11) {
			@Override
			public Filter<?> createFilter(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
				ObjectId<?> objectId = contextProvider.getObjectManager().readObjectId(bytes);
				return new InstanceOnlyFilter(objectId);
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

		private static ReverseEnumMap<Byte, ModKind> map = new ReverseEnumMap<Byte, Filter.ModKind>(ModKind.class);

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

	/**
	 * Whether this filter allows the event given as a parameter.
	 * 
	 * @param event
	 *            The event to be filtered.
	 * @return True of false as a result of filtering.
	 */
	public boolean matches(T event) {
		return matchesInternal(event);
	}

	protected boolean matchesInternal(T event) {
		return false;
	}

	protected boolean isAllowedEventKind(EventKind eventKind) {
		return false;
	}

}
