package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.JdwpObjectManager;
import gov.nasa.jpf.jdwp.command.ConvertibleEnum;
import gov.nasa.jpf.jdwp.command.ReverseEnumMap;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.vm.ArrayFields;
import gov.nasa.jpf.vm.BooleanArrayFields;
import gov.nasa.jpf.vm.ByteArrayFields;
import gov.nasa.jpf.vm.CharArrayFields;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.DoubleArrayFields;
import gov.nasa.jpf.vm.Fields;
import gov.nasa.jpf.vm.FloatArrayFields;
import gov.nasa.jpf.vm.IntArrayFields;
import gov.nasa.jpf.vm.LongArrayFields;
import gov.nasa.jpf.vm.ShortArrayFields;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public abstract class PrimitiveValue implements Value {
	public static enum Tag implements ConvertibleEnum<Byte, Tag> {
		ARRAY(91, Object[].class), BYTE(66, byte.class, ByteArrayFields.class) {
			@Override
			public Value value(Object object) {
				return new ByteValue((Byte) object);
			}

			@Override
			public Value readValue(ByteBuffer bytes) throws JdwpError {
				return new ByteValue(bytes.get());
			}

			@Override
			protected Value value(Fields fields, int index) {
				return new ByteValue(fields.getByteValue(index));
			}
		},
		CHAR(67, char.class, CharArrayFields.class) {
			@Override
			protected Value value(Fields fields, int index) {
				return new CharValue(fields.getCharValue(index));
			}

			@Override
			public Value value(Object object) {
				return new CharValue((Character) object);
			}

			@Override
			public Value readValue(ByteBuffer bytes) throws JdwpError {
				return new CharValue(bytes.getChar());
			}

		},
		OBJECT(76, Object.class), FLOAT(70, float.class, FloatArrayFields.class) {
			@Override
			public Value value(Object object) {
				return new FloatValue((Float) object);
			}

			@Override
			public Value readValue(ByteBuffer bytes) throws JdwpError {
				return new FloatValue(bytes.getFloat());
			}

		},
		DOUBLE(68, double.class, DoubleArrayFields.class) {

			@Override
			public Value value(Object object) {
				return new DoubleValue((Double) object);
			}

			@Override
			public Value readValue(ByteBuffer bytes) throws JdwpError {
				return new DoubleValue(bytes.getDouble());
			}

			@Override
			protected Value value(Fields fields, int index) {
				return new DoubleValue(fields.getDoubleValue(index));
			}
			
		},
		INT(73, int.class, IntArrayFields.class) {

			@Override
			public Value value(Object object) {
				return new IntegerValue((Integer) object);
			}

			@Override
			public Value readValue(ByteBuffer bytes) throws JdwpError {
				return new IntegerValue(bytes.getInt());
			}

			@Override
			protected Value value(Fields fields, int index) {
				return new IntegerValue(fields.getIntValue(index));
			}
			
		},
		LONG(74, long.class, LongArrayFields.class) {

			@Override
			public Value value(Object object) {
				return new LongValue((Long)object);
			}

			@Override
			public Value readValue(ByteBuffer bytes) throws JdwpError {
				return new LongValue(bytes.getLong());
			}

			@Override
			protected Value value(Fields fields, int index) {
				return new LongValue(fields.getLongValue(index));
			}
		},
		SHORT(83, short.class, ShortArrayFields.class) {

			@Override
			public Value value(Object object) {
				return new ShortValue((Short) object);
			}

			@Override
			public Value readValue(ByteBuffer bytes) throws JdwpError {
				return new ShortValue(bytes.getShort());
			}

			@Override
			protected Value value(Fields fields, int index) {
				return new ShortValue(fields.getShortValue(index));
			}
		},
		VOID(86, void.class) {

			@Override
			public Value value(Object object) {
				return new VoidValue();
			}

			@Override
			public Value readValue(ByteBuffer bytes) throws JdwpError {
				return new VoidValue();
			}
		},
		BOOLEAN(90, boolean.class, BooleanArrayFields.class) {

			@Override
			public Value value(Object object) {
				return new BooleanValue((Boolean) object);
			}

			@Override
			public Value readValue(ByteBuffer bytes) throws JdwpError {
				return new BooleanValue(bytes.get() != 0);
			}

			@Override
			protected Value value(Fields fields, int index) {
				return new BooleanValue(fields.getBooleanValue(index));
			}
			
		},
		STRING(115, String.class), THREAD(116, Thread.class), THREAD_GROUP(103, ThreadGroup.class), CLASS_LOADER(108, ClassLoader.class), CLASS_OBJECT(99,
				Class.class);

		private byte tagId;
		private final Class<?> clazz;
		protected Class<? extends ArrayFields> arrayClazz;

		Tag(int id, Class<?> clazz) {
			this.tagId = (byte) id;
			this.clazz = clazz;
		}
		Tag(int id, Class<?> clazz, Class<? extends ArrayFields> arrayClazz) {
			this.tagId = (byte) id;
			this.clazz = clazz;
			this.arrayClazz = arrayClazz;
		}

		public Value value(Object object) {
			return JdwpObjectManager.getInstance().getObjectId(object);
		}

		public Value readValue(ByteBuffer bytes) throws JdwpError {
			return JdwpObjectManager.getInstance().readObjectId(bytes);
		}

		private static final ReverseEnumMap<Byte, Tag> map = new ReverseEnumMap<Byte, Tag>(Tag.class);
		private static final HashMap<String, Tag> mapString = new HashMap<String, Tag>();
		private static final HashMap<Class<? extends Fields>, Tag> mapArrayClazz = new HashMap<Class<? extends Fields>, Tag>();

		static {
			for (Tag tag : values()) {
				if (tag.clazz != null) {
					mapString.put(tag.clazz.getName(), tag);
				}
				if (tag.arrayClazz != null) {
					mapArrayClazz.put(tag.arrayClazz, tag);
				}
			}
		}

		public static Value bytesToValue(ByteBuffer bytes) throws JdwpError {
			Tag tag = ARRAY.convert(bytes.get());
			return tag.readValue(bytes);
		}

		public static Value taggedObjectToValue(byte tagByte, Object object) throws JdwpError {
			return map.get(tagByte).value(object);
		}
		
		protected Value value(Fields fields, int index) {
			throw new RuntimeException("value get for Fields instance: " + fields + " is not by design implemented. This shouldn't happened!");
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
		public static Value arrayFieldToValue(Fields fields, int position) {
			return mapArrayClazz.get(fields.getClass()).value(fields, position);
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
