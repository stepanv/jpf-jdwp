package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.command.ArrayReferenceCommand;
import gov.nasa.jpf.jdwp.command.ConvertibleEnum;
import gov.nasa.jpf.jdwp.command.ObjectReferenceCommand;
import gov.nasa.jpf.jdwp.command.ReverseEnumMap;
import gov.nasa.jpf.jdwp.command.StackFrameCommand;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.vm.ArrayFields;
import gov.nasa.jpf.vm.BooleanArrayFields;
import gov.nasa.jpf.vm.ByteArrayFields;
import gov.nasa.jpf.vm.CharArrayFields;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.DoubleArrayFields;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Fields;
import gov.nasa.jpf.vm.FloatArrayFields;
import gov.nasa.jpf.vm.IntArrayFields;
import gov.nasa.jpf.vm.LongArrayFields;
import gov.nasa.jpf.vm.ShortArrayFields;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.VM;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * The common abstract class for all primitive values that are a subset of
 * common data type <i>value</i> according to the JDWP Specification as stated
 * in the table of Detailed Command Information section.
 * 
 * @see Value
 * 
 * @author stepan
 * 
 */
public abstract class PrimitiveValue implements Value {

  public static enum Tag implements ConvertibleEnum<Byte, Tag> {
    ARRAY(91, Object[].class), BYTE(66, byte.class, ByteArrayFields.class) {

      @Override
      public Value value(ElementInfo elementInfo, FieldInfo fieldInfo) {
        return new ByteValue(elementInfo.getByteField(fieldInfo));
      }

      @Override
      public Value value(Object object) {
        return new ByteValue((Byte) object);
      }

      @Override
      public Value value(ElementInfo array, int position) {
        return new CharValue(array.getCharElement(position));
      }

      @Override
      public Value readValue(ByteBuffer bytes) {
        return new ByteValue(bytes.get());
      }

      @Override
      public Value peekValue(StackFrame stackFrame) {
        return new ByteValue((byte) stackFrame.peek());
      }
    },
    CHAR(67, char.class, CharArrayFields.class) {

      @Override
      public Value value(ElementInfo elementInfo, FieldInfo fieldInfo) {
        return new CharValue(elementInfo.getCharField(fieldInfo));
      }

      @Override
      public Value value(Object object) {
        return new CharValue((Character) object);
      }

      @Override
      public Value value(ElementInfo array, int position) {
        return new CharValue(array.getCharElement(position));
      }

      @Override
      public Value readValue(ByteBuffer bytes) {
        return new CharValue(bytes.getChar());
      }

      @Override
      public Value peekValue(StackFrame stackFrame) {
        return new CharValue((char) stackFrame.peek());
      }

    },
    OBJECT(76, Object.class), FLOAT(70, float.class, FloatArrayFields.class) {

      @Override
      public Value value(ElementInfo elementInfo, FieldInfo fieldInfo) {
        return new DoubleValue(elementInfo.getDoubleField(fieldInfo));
      }

      @Override
      public Value value(Object object) {
        return new FloatValue((Float) object);
      }

      @Override
      public Value value(ElementInfo array, int position) {
        return new FloatValue(array.getFloatElement(position));
      }

      @Override
      public Value readValue(ByteBuffer bytes) {
        return new FloatValue(bytes.getFloat());
      }

      @Override
      public Value peekValue(StackFrame stackFrame) {
        return new FloatValue(stackFrame.peekFloat());
      }

    },
    DOUBLE(68, double.class, DoubleArrayFields.class) {

      @Override
      public Value value(ElementInfo elementInfo, FieldInfo fieldInfo) {
        return new DoubleValue(elementInfo.getDoubleField(fieldInfo));
      }

      @Override
      public Value value(Object object) {
        return new DoubleValue((Double) object);
      }

      @Override
      public Value value(ElementInfo array, int position) {
        return new DoubleValue(array.getDoubleElement(position));
      }

      @Override
      public Value readValue(ByteBuffer bytes) {
        return new DoubleValue(bytes.getDouble());
      }

      @Override
      public Value peekValue(StackFrame stackFrame) {
        return new DoubleValue(stackFrame.peekDouble());
      }

    },
    INT(73, int.class, IntArrayFields.class) {

      @Override
      public Value value(ElementInfo elementInfo, FieldInfo fieldInfo) {
        return new IntegerValue(elementInfo.getIntField(fieldInfo));
      }

      @Override
      public Value value(Object object) {
        return new IntegerValue((Integer) object);
      }

      @Override
      public Value value(ElementInfo array, int position) {
        return new IntegerValue(array.getIntElement(position));
      }

      @Override
      public Value readValue(ByteBuffer bytes) {
        return new IntegerValue(bytes.getInt());
      }

      @Override
      public Value peekValue(StackFrame stackFrame) {
        return new IntegerValue(stackFrame.peek());
      }
    },
    LONG(74, long.class, LongArrayFields.class) {

      @Override
      public Value value(ElementInfo elementInfo, FieldInfo fieldInfo) {
        return new LongValue(elementInfo.getLongField(fieldInfo));
      }

      @Override
      public Value value(Object object) {
        return new LongValue((Long) object);
      }

      @Override
      public Value value(ElementInfo array, int position) {
        return new LongValue(array.getLongElement(position));
      }

      @Override
      public Value readValue(ByteBuffer bytes) {
        return new LongValue(bytes.getLong());
      }

      @Override
      public Value peekValue(StackFrame stackFrame) {
        return new LongValue(stackFrame.peekLong());
      }

    },
    SHORT(83, short.class, ShortArrayFields.class) {

      @Override
      public Value value(ElementInfo elementInfo, FieldInfo fieldInfo) {
        return new ShortValue(elementInfo.getShortField(fieldInfo));
      }

      @Override
      public Value value(Object object) {
        return new ShortValue((Short) object);
      }

      @Override
      public Value value(ElementInfo array, int position) {
        return new ShortValue(array.getShortElement(position));
      }

      @Override
      public Value readValue(ByteBuffer bytes) {
        return new ShortValue(bytes.getShort());
      }

      @Override
      public Value peekValue(StackFrame stackFrame) {
        return new ShortValue((short) stackFrame.peek());
      }
    },
    VOID(86, void.class) {

      @Override
      public Value value(ElementInfo elementInfo, FieldInfo fieldInfo) {
        return new VoidValue();
      }

      @Override
      public Value value(Object object) {
        return new VoidValue();
      }

      @Override
      public Value value(ElementInfo array, int position) {
        return new VoidValue();
      }

      @Override
      public Value readValue(ByteBuffer bytes) {
        return new VoidValue();
      }

      @Override
      public Value peekValue(StackFrame stackFrame) {
        return new VoidValue();
      }
    },
    BOOLEAN(90, boolean.class, BooleanArrayFields.class) {

      @Override
      public Value value(ElementInfo elementInfo, FieldInfo fieldInfo) {
        return new BooleanValue(elementInfo.getBooleanField(fieldInfo));
      }

      @Override
      public Value value(Object object) {
        return new BooleanValue((Boolean) object);
      }

      @Override
      public Value value(ElementInfo array, int position) {
        return new BooleanValue(array.getBooleanElement(position));
      }

      @Override
      public Value readValue(ByteBuffer bytes) {
        return new BooleanValue(bytes.get() != 0);
      }

      @Override
      public Value peekValue(StackFrame stackFrame) {
        return new BooleanValue(stackFrame.peek() != 0);
      }

    },
    STRING(115, String.class), THREAD(116, Thread.class), THREAD_GROUP(103, ThreadGroup.class), CLASS_LOADER(108, ClassLoader.class), CLASS_OBJECT(
        99, Class.class);

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

    /**
     * <p>
     * Transforms an object into the {@link Value} instance. <br/>
     * Check out overridden methods in {@link Tag} for specific implementations
     * of primitive types.
     * </p>
     * <p>
     * This method is used to query values from object instances as in
     * {@link StackFrameCommand}. TODO is there a better way how to get a value
     * from a StackFrame?
     * </p>
     * 
     * @param object
     *          Object to transform.
     * @return Value instance.
     */
    public Value value(Object object) {
      ElementInfo elementInfo;
      if (object instanceof Number) {
        elementInfo = VM.getVM().getHeap().get((Integer) object);
      } else {
        elementInfo = (ElementInfo) object;
      }
      return JdwpObjectManager.getInstance().getObjectId(elementInfo);
    }

    /**
     * <p>
     * Gets {@link Value} instance for the given {@link ElementInfo} non-array
     * instance for the given field.<br/>
     * Check out overridden methods in {@link Tag} for specific implementations
     * of primitive types.
     * </p>
     * <p>
     * This method is used to query values from object instances as in
     * {@link ObjectReferenceCommand}.
     * </p>
     * 
     * @param elementInfo
     *          The instance of object to query.
     * @param fieldInfo
     *          The info object of the field.
     * @return The {@link Value} instance for the value of the given field.
     */
    public Value value(ElementInfo elementInfo, FieldInfo fieldInfo) {
      return objRefToValue(elementInfo.getReferenceField(fieldInfo));
    }

    /**
     * <p>
     * Gets {@link Value} instance for the given {@link ElementInfo} array
     * instance at given position.<br/>
     * Check out overridden methods in {@link Tag} for specific implementations
     * of primitive types.
     * </p>
     * <p>
     * This method is used to query values from arrays as in
     * {@link ArrayReferenceCommand}.
     * </p>
     * 
     * @param array
     *          The array to get the value from.
     * @param position
     *          The position of the element in the array.
     * @return The {@link Value} instance for the given position.
     */
    public Value value(ElementInfo array, int position) {
      return objRefToValue(array.getReferenceElement(position));
    }

    private static Value objRefToValue(int objRef) {
      ElementInfo fieldElementInfo = VM.getVM().getHeap().get(objRef);
      return JdwpObjectManager.getInstance().getObjectId(fieldElementInfo);
    }

    /**
     * Peeks the {@link Value} instance from the stack frame.<br/>
     * This is the common/default implementation.<br/>
     * Check out overridden methods in {@link Tag} for specific implementations
     * of primitive types.
     * 
     * @param stackFrame
     *          Where to peek the value.
     * @return Value instance
     */
    public Value peekValue(StackFrame stackFrame) {
      ElementInfo result = VM.getVM().getHeap().get(stackFrame.peek());
      return JdwpObjectManager.getInstance().getObjectId(result);
    }

    /**
     * Reads the {@link Value} instance from the byte buffer.<br/>
     * Check out overridden methods in {@link Tag} for specific implementations
     * of primitive types.
     * 
     * @param bytes
     *          Source byte buffer
     * @return Value instance
     * @throws JdwpError
     */
    public Value readValue(ByteBuffer bytes) {
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

    public static Tag fieldToTag(FieldInfo field) {
      return Tag.classInfoToTag(field.getTypeClassInfo());
    }

  }

  private Tag tag;

  public PrimitiveValue(Tag tag) {
    this.tag = tag;
  }

  @Override
  public abstract void writeUntagged(DataOutputStream os) throws IOException;

  @Override
  public void writeTagged(DataOutputStream os) throws IOException {
    os.writeByte(tag.tagId);
    writeUntagged(os);
  }
}
