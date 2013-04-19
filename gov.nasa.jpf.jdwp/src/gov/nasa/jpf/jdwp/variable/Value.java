package gov.nasa.jpf.jdwp.variable;

import gov.nasa.jpf.jdwp.command.IdentifiableEnum;
import gov.nasa.jpf.jdwp.command.ReverseEnumMap;
import gov.nasa.jpf.jdwp.command.VirtualMachineCommand;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.id.object.ObjectId;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * A value retrieved from the target VM. The first byte is a signature byte
 * which is used to identify the type. See {@link Tag} for the possible values
 * of this byte. It is followed immediately by the value itself. This value can
 * be an {@link ObjectId} (see Get ID Sizes (
 * {@link VirtualMachineCommand#IDSIZES})) or a primitive value (1 to 8 bytes).<br/>
 * More details about each value type can be found in the next table.
 * </p>
 * 
 * @author stepan
 * 
 */
public abstract class Value {
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
			throw new RuntimeException("NOT IMPLEMENTED YET!");
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

	public Value(Tag tag) {
		this.tag = tag;
	}

	public abstract void write(DataOutputStream os) throws IOException;

	public void writeTagged(DataOutputStream os) throws IOException {
		os.writeByte(tag.tagId);
		write(os);
	}

}
