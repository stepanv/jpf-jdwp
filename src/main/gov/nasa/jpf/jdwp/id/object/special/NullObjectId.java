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

package gov.nasa.jpf.jdwp.id.object.special;

import gov.nasa.jpf.jdwp.exception.id.object.InvalidClassLoaderException;
import gov.nasa.jpf.jdwp.exception.id.object.InvalidClassObjectException;
import gov.nasa.jpf.jdwp.exception.id.object.InvalidThreadException;
import gov.nasa.jpf.jdwp.exception.id.object.NullPointerObjectException;
import gov.nasa.jpf.jdwp.id.object.ArrayId;
import gov.nasa.jpf.jdwp.id.object.ClassLoaderId;
import gov.nasa.jpf.jdwp.id.object.ClassObjectId;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ObjectIdImpl;
import gov.nasa.jpf.jdwp.id.object.StringId;
import gov.nasa.jpf.jdwp.id.object.ThreadGroupId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.DynamicElementInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>
 * A special object used to represent null object reference in SuT.
 * </p>
 * <p>
 * Note that even though null object doesn't represent Thread nor Classloader
 * nor any other meaningful object it is still required to have this instance
 * compatible with the special objects types (the subtypes of {@link ObjectId})
 * so that the JDWP code is compilable and class cast exceptions free.
 * </p>
 * <p>
 * <h2>JDWP Specification</h2>
 * An objectID of 0 represents a null object.
 * </p>
 * 
 * @author stepan
 * 
 */
public class NullObjectId extends ObjectIdImpl implements ThreadId, ClassLoaderId, ClassObjectId, ArrayId, StringId, ThreadGroupId {

  protected NullObjectId() {
    super(Tag.OBJECT, 0, -1);
  }

  public static NullObjectId getInstance() {
    return instance;
  }

  /**
   * Helper method that writes {@link NullObjectId} instance to the stream.
   * 
   * @param os
   *          Where to write the null object
   * @throws IOException
   *           If IO error occurs
   */
  public static void instantWrite(DataOutputStream os) throws IOException {
    instance.write(os);
  }

  /**
   * Helper method that writes {@link NullObjectId} tagged instance to the
   * stream.
   * 
   * @param os
   *          Where to write the null object
   * @throws IOException
   *           If IO error occurs
   */
  public static void instanceWriteTagged(DataOutputStream os) throws IOException {
    instance.writeTagged(os);
  }

  private static final NullObjectId instance = new NullObjectId();

  @Override
  public void push(StackFrame frame) {
    frame.pushRef(-1);
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nasa.jpf.jdwp.id.object.ThreadId#getThreadInfo()
   */
  @Override
  public ThreadInfo getThreadInfo() throws InvalidThreadException {
    throw new InvalidThreadException(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nasa.jpf.jdwp.id.object.ClassLoaderId#getClassLoaderInfo()
   */
  @Override
  public ClassLoaderInfo getClassLoaderInfo() throws InvalidClassLoaderException {
    throw new InvalidClassLoaderException(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nasa.jpf.jdwp.id.object.ClassObjectId#getClassInfo()
   */
  @Override
  public ClassInfo getClassInfo() throws InvalidClassObjectException {
    throw new InvalidClassObjectException(this);
  }

  @Override
  public DynamicElementInfo get() throws NullPointerObjectException {
    throw new NullPointerObjectException();
  }

  @Override
  public DynamicElementInfo getModifiable() throws NullPointerObjectException {
    throw new NullPointerObjectException();
  }
  
  

}
