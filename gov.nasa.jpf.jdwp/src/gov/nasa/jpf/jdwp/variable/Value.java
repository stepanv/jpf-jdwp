package gov.nasa.jpf.jdwp.variable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class Value {
	public static enum Tag {
		ARRAY(91) ,
		BYTE(66){
			@Override
			public Value value(Object object) {
				return new ByteValue((Byte) object);
			}
		},
		CHAR(67){
			@Override
			public Value value(Object object) {
				return new CharValue((Character)object);
			}
		},
		OBJECT(76),
		FLOAT(70){
			@Override
			public Value value(Object object) {
				return new FloatValue((Float)object);
			}
		},
		DOUBLE((byte)68),
		INT(73),
		LONG(74),
		SHORT(83),
		VOID(86),
		BOOLEAN(90),
		STRING(115),
		THREAD(116),
		THREAD_GROUP(103),
		CLASS_LOADER(108),
		CLASS_OBJECT(99);
		
		private byte id;

		Tag(int id) {
			this.id = (byte)id;
		}
		public Value value(Object object) {
			return null;
		}
		
		private static final Map<Byte, Tag> IDTAGF_VALUE_MAP = new HashMap<Byte, Tag>();
		static {
		    for (Tag type : Tag.values()) {
		        IDTAGF_VALUE_MAP.put(type.id, type);
		    }
		}
		
		public static Value taggedObjectToValue(byte tagByte, Object object) {
			return IDTAGF_VALUE_MAP.get(tagByte).value(object);
		}
	}


	private Tag tag;
	
	public Value(Tag tag) {
		this.tag = tag;
	}


	public abstract void write(DataOutputStream os) throws IOException;
	
	public void writeTagged(DataOutputStream os) throws IOException {
		os.writeByte(tag.id);
		write(os);
	}

}
