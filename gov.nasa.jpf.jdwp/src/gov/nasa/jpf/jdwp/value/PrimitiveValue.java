package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.JdwpObjectManager;
import gov.nasa.jpf.jdwp.command.ConvertibleEnum;
import gov.nasa.jpf.jdwp.command.ReverseEnumMap;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public abstract class PrimitiveValue implements Value {
	public static enum Tag implements ConvertibleEnum<Byte, Tag> {
		ARRAY(91), BYTE(66) {
			@Override
			public Value value(Object object) {
				return new ByteValue((Byte) object);
			}

			@Override
			public Value readValue(ByteBuffer bytes) throws JdwpError {
				return new ByteValue(bytes.get());
			}
		},
		CHAR(67) {
			@Override
			public Value value(Object object) {
				return new CharValue((Character) object);
			}

			@Override
			public Value readValue(ByteBuffer bytes) throws JdwpError {
				return new CharValue(bytes.getChar());
			}

		},
		OBJECT(76), FLOAT(70) {
			@Override
			public Value value(Object object) {
				return new FloatValue((Float) object);
			}

			@Override
			public Value readValue(ByteBuffer bytes) throws JdwpError {
				return new FloatValue(bytes.getFloat());
			}

		},
		DOUBLE(68) {

			@Override
			public Value value(Object object) {
				return new DoubleValue((Double) object);
			}

			@Override
			public Value readValue(ByteBuffer bytes) throws JdwpError {
				return new DoubleValue(bytes.getDouble());
			}
		},
		INT(73) {

			@Override
			public Value value(Object object) {
				return new IntegerValue((Integer) object);
			}

			@Override
			public Value readValue(ByteBuffer bytes) throws JdwpError {
				return new IntegerValue(bytes.getInt());
			}
		},
		LONG(74) {

			@Override
			public Value value(Object object) {
				// TODO Auto-generated method stub
				return super.value(object);
			}

			@Override
			public Value readValue(ByteBuffer bytes) throws JdwpError {
				// TODO Auto-generated method stub
				return super.readValue(bytes);
			}
		},
		SHORT(83) {

			@Override
			public Value value(Object object) {
				return new ShortValue((Short) object);
			}

			@Override
			public Value readValue(ByteBuffer bytes) throws JdwpError {
				return new ShortValue(bytes.getShort());
			}
		},
		VOID(86) {

			@Override
			public Value value(Object object) {
				return new VoidValue();
			}

			@Override
			public Value readValue(ByteBuffer bytes) throws JdwpError {
				return new VoidValue();
			}
		},
		BOOLEAN(90) {

			@Override
			public Value value(Object object) {
				return new BooleanValue((Boolean) object);
			}

			@Override
			public Value readValue(ByteBuffer bytes) throws JdwpError {
				return new BooleanValue(bytes.get() != 0);
			}
		},
		STRING(115, String.class), THREAD(116, Thread.class), THREAD_GROUP(103, ThreadGroup.class), CLASS_LOADER(108, ClassLoader.class), CLASS_OBJECT(99,
				Class.class);

		private byte tagId;
		private final Class<?> clazz;

		Tag(int id) {
			this(id, null);
		}

		Tag(int id, Class<?> clazz) {
			this.tagId = (byte) id;
			this.clazz = clazz;
		}

		public Value value(Object object) {
			return JdwpObjectManager.getInstance().getObjectId(object);
		}

		public Value readValue(ByteBuffer bytes) throws JdwpError {
			return JdwpObjectManager.getInstance().readObjectId(bytes);
		}

		private static final ReverseEnumMap<Byte, Tag> map = new ReverseEnumMap<Byte, Tag>(Tag.class);
		private static final HashMap<String, Tag> mapString = new HashMap<String, Tag>();

		static {
			for (Tag tag : values()) {
				mapString.put(tag.clazz.getName(), tag);
			}
		}

		public static Value bytesToValue(ByteBuffer bytes) throws JdwpError {
			return ARRAY.convert(bytes.get()).readValue(bytes);
		}

		public static Value taggedObjectToValue(byte tagByte, Object object) throws JdwpError {
			return map.get(tagByte).value(object);
		}

		public static Tag classInfoToTag(ClassInfo classInfo) {
			if (mapString.containsKey(classInfo.getName())) {
				return mapString.get(classInfo.getName());
			}
			if (classInfo.getSuperClass() != null) {
				return classInfoToTag(classInfo.getSuperClass());
			}
			throw new RuntimeException("NOT IMPLEMENTED .. for: " + classInfo);
		}

		@Override
		public Byte identifier() {
			return tagId;
		}

		@Override
		public Tag convert(Byte val) throws JdwpError {
			return map.get(val);
		}
	}

	private Tag tag;

	public PrimitiveValue(Tag tag) {
		this.tag = tag;
	}

	public abstract void write(DataOutputStream os) throws IOException;

	public void writeTagged(DataOutputStream os) throws IOException {
		os.writeByte(tag.tagId);
		write(os);
	}

}
