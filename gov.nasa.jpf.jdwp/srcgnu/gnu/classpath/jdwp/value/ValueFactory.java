/* ValueFactory.java -- factory to create JDWP Values
   Copyright (C) 2007 Free Software Foundation

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */

package gnu.classpath.jdwp.value;

import gnu.classpath.jdwp.JdwpConstants;
import gnu.classpath.jdwp.VMIdManager;
import gnu.classpath.jdwp.exception.InvalidClassException;
import gnu.classpath.jdwp.exception.InvalidObjectException;
import gnu.classpath.jdwp.exception.InvalidTagException;
import gnu.classpath.jdwp.exception.JdwpInternalErrorException;
import gnu.classpath.jdwp.id.ArrayId;
import gnu.classpath.jdwp.id.ClassLoaderId;
import gnu.classpath.jdwp.id.ObjectId;
import gnu.classpath.jdwp.id.StringId;
import gnu.classpath.jdwp.id.ThreadId;
import gnu.classpath.jdwp.util.JdwpString;
import gov.nasa.jpf.jvm.BooleanFieldInfo;
import gov.nasa.jpf.jvm.ByteFieldInfo;
import gov.nasa.jpf.jvm.CharFieldInfo;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.DoubleFieldInfo;
import gov.nasa.jpf.jvm.DynamicElementInfo;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.FieldInfo;
import gov.nasa.jpf.jvm.FloatFieldInfo;
import gov.nasa.jpf.jvm.IntegerFieldInfo;
import gov.nasa.jpf.jvm.LongFieldInfo;
import gov.nasa.jpf.jvm.ShortFieldInfo;

import java.nio.ByteBuffer;

/**
 * A factory to create JDWP Values.
 *
 * @author Kyle Galloway <kgallowa@redhat.com>
 */
public class ValueFactory
{
  /**
   * Creates a new Value of appropriate type for the value in the ByteBuffer
   * by reading the tag byte from the front of the buffer.
   *
   * @param bb contains the Object
   * @return A new Value of appropriate type
   * @throws JdwpInternalErrorException
   * @throws InvalidObjectException
   */
  public static Value createFromTagged(ByteBuffer bb)
    throws JdwpInternalErrorException, InvalidObjectException, InvalidTagException
  {
    return create(bb, bb.get());
  }

  /**
   * Creates a new Value of appropriate type for the value in the ByteBuffer
   * by checking the type of the Class passed in.
   *
   * @param bb contains the Object
   * @param type a Class representing the type of the value in the ByteBuffer
   * @return A new Value of appropriate type
   * @throws JdwpInternalErrorException
   * @throws InvalidObjectException
   */
  public static Value createFromUntagged(ByteBuffer bb, Class type)
  throws JdwpInternalErrorException, InvalidObjectException, InvalidClassException
  {
    byte tag = getTagForClass(type);

    try
      {
        return create(bb, tag);
      }
    catch (InvalidTagException ite)
      {
        throw new InvalidClassException(ite);
      }
  }
  
  public static Value createFromObjectTagged(Object value, byte tag) throws InvalidObjectException {
	  Value val = null;
	    switch(tag)
	    {
	      case JdwpConstants.Tag.BYTE:
	    	  val = new ByteValue(((Byte) value).byteValue());
	        break;
	      case JdwpConstants.Tag.BOOLEAN:
	    	  val = new BooleanValue(((Boolean) value).booleanValue());
	        break;
	      case JdwpConstants.Tag.CHAR:
	    	  val = new CharValue(((Character) value).charValue());
	        break;
	      case JdwpConstants.Tag.SHORT:
	    	  val = new ShortValue(((Short) value).shortValue());
	        break;
	      case JdwpConstants.Tag.INT:
	    	  val = new IntValue(((Integer) value).intValue());
	        break;
	      case JdwpConstants.Tag.FLOAT:
	    	  val = new FloatValue(((Float) value).floatValue());
	        break;
	      case JdwpConstants.Tag.LONG:
	    	  val = new LongValue(((Long) value).longValue());
	        break;
	      case JdwpConstants.Tag.DOUBLE:
	    	  val = new DoubleValue(((Double) value).doubleValue());
	        break;
	      case JdwpConstants.Tag.VOID:
	        val = new VoidValue();
	        break;
	      case JdwpConstants.Tag.ARRAY:
	      case JdwpConstants.Tag.THREAD:
	      case JdwpConstants.Tag.OBJECT:
	      case JdwpConstants.Tag.THREAD_GROUP:
	      case JdwpConstants.Tag.CLASS_LOADER:
	      case JdwpConstants.Tag.CLASS_OBJECT:
	      case JdwpConstants.Tag.STRING:
	    	  
	    	  ObjectId oid = VMIdManager.getDefault().getObjectId(value);
	    	  
	    	  // JDI has just types: ReferenceType and PrimitiveType (and theirs implementators)
	    	  // Thus we get only Object tag for the corresponding ReferenceType
	    	  // On the other hand we must return the right tag so that JDI's Value can be correctly instatiated
	    	  
	    	  val = oid.factory();
//	    	  if (oid instanceof StringId) {
//	    		  val = new StringValue((DynamicElementInfo)oid.getObject());
//	    	  } else {
//	    		  val = new ObjectValue(oid.getObject());
//
//	    	  }
	        //throw new RuntimeException("not implemented"); // TODO implement also objects
	        
//ObjectId oid = VMIdManager.getDefault().getObjectId(value); // TODO what if a String appears here? it's so weird
//val = 
//break;
	    	  //ObjectId oid = VMIdManager.getDefault().readObjectId(bb);
	        
	        //val = new ObjectValue(oid.getObject());
	        //break;
	      
//	        val = new StringValue((String) value);
	        break;
	      default:
	        //throw new InvalidTagException(tag);
	    	  throw new InvalidObjectException(new InvalidTagException(tag));
	    }

	    return val;
  }

  /**
   * Creates a new Value of appropriate type for the value in the ByteBuffer.
   *
   * @param bb contains the Object
   * @param tag a byte representing the type of the object
   * @return A new Value of appropriate type
   * @throws JdwpInternalErrorException
   * @throws InvalidObjectException
   */
  private static Value create(ByteBuffer bb, byte tag)
    throws JdwpInternalErrorException, InvalidObjectException, InvalidTagException
  {
    Value val = null;
    switch(tag)
    {
      case JdwpConstants.Tag.BYTE:
        val = new ByteValue(bb.get());
        break;
      case JdwpConstants.Tag.BOOLEAN:
        val = new BooleanValue((bb.get() != 0));
        break;
      case JdwpConstants.Tag.CHAR:
        val = new CharValue(bb.getChar());
        break;
      case JdwpConstants.Tag.SHORT:
        val = new ShortValue(bb.getShort());
        break;
      case JdwpConstants.Tag.INT:
        val = new IntValue(bb.getInt());
        break;
      case JdwpConstants.Tag.FLOAT:
        val = new FloatValue(bb.getFloat());
        break;
      case JdwpConstants.Tag.LONG:
        val = new LongValue(bb.getLong());
        break;
      case JdwpConstants.Tag.DOUBLE:
        val = new DoubleValue(bb.getDouble());
        break;
      case JdwpConstants.Tag.VOID:
        val = new VoidValue();
        break;
      case JdwpConstants.Tag.ARRAY:
      case JdwpConstants.Tag.THREAD:
      case JdwpConstants.Tag.OBJECT:
      case JdwpConstants.Tag.THREAD_GROUP:
      case JdwpConstants.Tag.CLASS_LOADER:
      case JdwpConstants.Tag.CLASS_OBJECT:
      case JdwpConstants.Tag.STRING:
        ObjectId oid = VMIdManager.getDefault().readObjectId(bb);
        val = oid.factory();
        break;
      
      default:
        throw new InvalidTagException(tag);
    }

    return val;
  }

  /**
   * Creates a tag for the type of the class.
   *
   * @param klass the type to get a tag for
   * @return a byte tag representing the class
   * @throws JdwpInternalErrorException
   * @throws InvalidObjectException
   */
  private static byte getTagForClass(Class klass)
    throws JdwpInternalErrorException
  {
    byte tag;

    if (klass.isPrimitive())
      {
        if (klass == byte.class)
          tag = JdwpConstants.Tag.BYTE;
        else if (klass == boolean.class)
          tag = JdwpConstants.Tag.BOOLEAN;
        else if (klass == char.class)
          tag = JdwpConstants.Tag.CHAR;
        else if (klass == short.class)
          tag = JdwpConstants.Tag.SHORT;
        else if (klass == int.class)
          tag = JdwpConstants.Tag.INT;
        else if (klass == float.class)
          tag = JdwpConstants.Tag.FLOAT;
        else if (klass == long.class)
          tag = JdwpConstants.Tag.LONG;
        else if (klass == double.class)
          tag = JdwpConstants.Tag.DOUBLE;
        else if (klass == void.class)
          tag = JdwpConstants.Tag.VOID;
        else
          throw new JdwpInternalErrorException("Invalid primitive class");
      }
    else
      {
        tag = JdwpConstants.Tag.OBJECT;
      }

    return tag;
  }

  /**
   * Create a value type for an Object of type determined by a Class.  This is
   * a special case where a value needs to be created, but the value to create
   * it for is already in an object, not in a buffer.
   *
   * @param value the Object to convert to a Value
   * @param type the Class type of the object
   * @return a new Value representing this object
   */
  public static Value createFromObject(Object value, Class type)
  {
    Value val = null;

    if (type.isPrimitive())
      {
        if (type == byte.class)
          val = new ByteValue(((Byte) value).byteValue());
        else if (type == boolean.class)
          val = new BooleanValue(((Boolean) value).booleanValue());
        else if (type == char.class)
          val = new CharValue(((Character) value).charValue());
        else if (type == short.class)
          val = new ShortValue(((Short) value).shortValue());
        else if (type == int.class)
          val = new IntValue(((Integer) value).intValue());
        else if (type == float.class)
          val = new FloatValue(((Float) value).floatValue());
        else if (type == long.class)
          val = new LongValue(((Long) value).longValue());
        else if (type == double.class)
          val = new DoubleValue(((Double) value).doubleValue());
        else if (type == void.class)
          val = new VoidValue();
      }
    else
      {
        if (type.isAssignableFrom(String.class))
          val = new StringValue ((String) value);
        else
          val = new ObjectValue(value);
      }

    return val;
  }

public static Value createFromObject(Object value, FieldInfo field) throws InvalidObjectException {
	Value val = null;

    if (!field.isReference())
      {
        if (field instanceof ByteFieldInfo)
          val = new ByteValue(((Byte) value).byteValue());
        else if (field instanceof BooleanFieldInfo)
          val = new BooleanValue(((Boolean) value).booleanValue());
        else if (field instanceof CharFieldInfo)
          val = new CharValue(((Character) value).charValue());
        else if (field instanceof ShortFieldInfo)
          val = new ShortValue(((Short) value).shortValue());
        else if (field instanceof IntegerFieldInfo)
          val = new IntValue(((Integer) value).intValue());
        else if (field instanceof FloatFieldInfo)
          val = new FloatValue(((Float) value).floatValue());
        else if (field instanceof LongFieldInfo)
          val = new LongValue(((Long) value).longValue());
        else if (field instanceof DoubleFieldInfo)
          val = new DoubleValue(((Double) value).doubleValue());
        // TODO originally there was also void type ... we don't have void fields .. do we?
//        else if (type == void.class) 
//          val = new VoidValue();
      }
    else
      { 
    	// TODO maybe forward decision about Strings to the end
    	// because do we really need to keep track of Strings as different objects?
    	// it could be just fine to treat them as ObjectValues (except for sending them through JDWP)
    	 ObjectId oid = VMIdManager.getDefault().getObjectId(value);
    	 val = oid.factory();
      }

    return val;
}
}
