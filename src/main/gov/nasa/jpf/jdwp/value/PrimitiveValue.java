/* 
   Copyright (C) 2013 Stepan Vavra

This file is part of (Java Debug Wire Protocol) JDWP for 
Java PathFinder (JPF) project.

JDWP for JPF is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

JDWP for JPF is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 
 */

package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.command.ArrayReferenceCommand;
import gov.nasa.jpf.jdwp.command.ConvertibleEnum;
import gov.nasa.jpf.jdwp.command.ObjectReferenceCommand;
import gov.nasa.jpf.jdwp.command.ReverseEnumMap;
import gov.nasa.jpf.jdwp.command.StackFrameCommand;
import gov.nasa.jpf.jdwp.exception.IllegalArgumentException;
import gov.nasa.jpf.jdwp.exception.InvalidTagException;
import gov.nasa.jpf.jdwp.exception.JdwpException;
import gov.nasa.jpf.jdwp.exception.id.object.InvalidObjectException;
import gov.nasa.jpf.jdwp.id.JdwpIdManager;
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

    /**
     * <p>
     * <h2>JDWP Specification</h2>
     * '[' - an array object (objectID size).
     * </p>
     * <p>
     * Note that this instance uses the default methods implementation from
     * {@link Tag}.
     * </p>
     */
    ARRAY(91, Object[].class),

    /**
     * <p>
     * <h2>JDWP Specification</h2>
     * 'B' - a byte value (1 byte).
     * </p>
     */
    BYTE(66, byte.class, ByteArrayFields.class) {

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

    /**
     * <p>
     * <h2>JDWP Specification</h2>
     * 'C' - a character value (2 bytes).
     * </p>
     */
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

    /**
     * <p>
     * <h2>JDWP Specification</h2>
     * 'L' - an object (objectID size).
     * </p>
     * <p>
     * Note that this instance uses the default methods implementation from
     * {@link Tag}.
     * </p>
     */
    OBJECT(76, Object.class),

    /**
     * <p>
     * <h2>JDWP Specification</h2>
     * 'F' - a float value (4 bytes).
     * </p>
     */
    FLOAT(70, float.class, FloatArrayFields.class) {

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

    /**
     * <p>
     * <h2>JDWP Specification</h2>
     * 'D' - a double value (8 bytes).
     * </p>
     */
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

    /**
     * <p>
     * <h2>JDWP Specification</h2>
     * 'I' - an int value (4 bytes).
     * </p>
     */
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

    /**
     * <p>
     * <h2>JDWP Specification</h2>
     * 'J' - a long value (8 bytes).
     * </p>
     */
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

    /**
     * <p>
     * <h2>JDWP Specification</h2>
     * 'S' - a short value (2 bytes).
     * </p>
     */
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

    /**
     * <p>
     * <h2>JDWP Specification</h2>
     * 'V' - a void value (no bytes).
     * </p>
     */
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

    /**
     * <p>
     * <h2>JDWP Specification</h2>
     * 'Z' - a boolean value (1 byte).
     * </p>
     */
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

    /**
     * <p>
     * <h2>JDWP Specification</h2>
     * 's' - a String object (objectID size).
     * </p>
     * <p>
     * Note that this instance uses the default methods implementation from
     * {@link Tag}.
     * </p>
     */
    STRING(115, String.class),

    /**
     * <p>
     * <h2>JDWP Specification</h2>
     * 't' - a Thread object (objectID size).
     * </p>
     * <p>
     * Note that this instance uses the default methods implementation from
     * {@link Tag}.
     * </p>
     */
    THREAD(116, Thread.class),

    /**
     * <p>
     * <h2>JDWP Specification</h2>
     * 'g' - a ThreadGroup object (objectID size).
     * </p>
     * <p>
     * Note that this instance uses the default methods implementation from
     * {@link Tag}.
     * </p>
     */
    THREAD_GROUP(103, ThreadGroup.class),

    /**
     * <p>
     * <h2>JDWP Specification</h2>
     * 'l' - a ClassLoader object (objectID size).
     * </p>
     * <p>
     * Note that this instance uses the default methods implementation from
     * {@link Tag}.
     * </p>
     */
    CLASS_LOADER(108, ClassLoader.class),

    /**
     * <p>
     * <h2>JDWP Specification</h2>
     * 'c' - a class object object (objectID size).
     * </p>
     * <p>
     * Note that this instance uses the default methods implementation from
     * {@link Tag}.
     * </p>
     */
    CLASS_OBJECT(99, Class.class);

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
     * {@link StackFrameCommand}.
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
      return JdwpIdManager.getInstance().getObjectId(elementInfo);
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
      return JdwpIdManager.getInstance().getObjectId(fieldElementInfo);
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
      return JdwpIdManager.getInstance().getObjectId(result);
    }

    /**
     * Reads the {@link Value} instance from the byte buffer.<br/>
     * Check out overridden methods in {@link Tag} for specific implementations
     * of primitive types.
     * 
     * @param bytes
     *          Source byte buffer
     * @return Value instance
     * @throws InvalidObjectException
     *           if the ID in the byte buffer is not a valid ID or has been
     *           garbage collected
     */
    public Value readValue(ByteBuffer bytes) throws InvalidObjectException {
      return JdwpIdManager.getInstance().readObjectId(bytes);
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

    public static Value bytesToValue(ByteBuffer bytes) throws JdwpException {
      Tag tag = ARRAY.convert(bytes.get());
      return tag.readValue(bytes);
    }

    public static Value taggedObjectToValue(byte tagByte, Object object) throws JdwpException {
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
    public Tag convert(Byte tagId) throws InvalidTagException {
      try {
        return map.get(tagId);
      } catch (IllegalArgumentException e) {
        throw new InvalidTagException(tagId, e);
      }
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
