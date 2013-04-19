package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.JdwpObjectManager;
import gov.nasa.jpf.jdwp.command.IdentifiableEnum;
import gov.nasa.jpf.jdwp.command.ReverseEnumMap;
import gov.nasa.jpf.jdwp.exception.JdwpError;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class PrimitiveValue implements Value {
	public static enum Tag implements IdentifiableEnum<Byte> {
		ARRAY(91), BYTE(66) {
			@Override
			public Value value(Object object) {
				return new ByteValue((Byte) object);
			}
		},
		CHAR(67) {
			@Override
			public Value value(Object object) {
				return new CharValue((Character) object);
			}
		},
		OBJECT(76), FLOAT(70) {
			@Override
			public Value value(Object object) {
				return new FloatValue((Float) object);
			}
		},
		DOUBLE(68), INT(73), LONG(74), SHORT(83), VOID(86), BOOLEAN(90), STRING(115), THREAD(116), THREAD_GROUP(103), CLASS_LOADER(108), CLASS_OBJECT(99);

		private byte tagId;

		Tag(int id) {
			this.tagId = (byte) id;
		}

		public Value value(Object object) {
			return JdwpObjectManager.getInstance().getObjectId(object);
		}

		private static final ReverseEnumMap<Byte, Tag> map = new ReverseEnumMap<Byte, Tag>(Tag.class);
		

		public static Value taggedObjectToValue(byte tagByte, Object object) throws JdwpError {
			return map.get(tagByte).value(object);
		}

		@Override
		public Byte identifier() {
			return tagId;
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
