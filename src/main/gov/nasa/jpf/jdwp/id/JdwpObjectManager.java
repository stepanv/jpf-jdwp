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

package gov.nasa.jpf.jdwp.id;

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.id.IdManager.IdFactory;
import gov.nasa.jpf.jdwp.id.object.ArrayId;
import gov.nasa.jpf.jdwp.id.object.ClassLoaderId;
import gov.nasa.jpf.jdwp.id.object.ClassObjectId;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ObjectIdManager;
import gov.nasa.jpf.jdwp.id.object.StringId;
import gov.nasa.jpf.jdwp.id.object.ThreadGroupId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.id.object.special.NullObjectId;
import gov.nasa.jpf.jdwp.id.type.ArrayTypeReferenceId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import java.nio.ByteBuffer;

public class JdwpObjectManager {

  private static class JdwpObjectManagerHolder {
    private static final JdwpObjectManager instance = new JdwpObjectManager();
  }

  public static JdwpObjectManager getInstance() {
    return JdwpObjectManagerHolder.instance;
  }

  ObjectIdManager objectIdManager = new ObjectIdManager();

  private IdManager<ReferenceTypeId, ClassInfo> referenceIdManager = new IdManager<ReferenceTypeId, ClassInfo>(
      new IdFactory<ReferenceTypeId, ClassInfo>() {

        @Override
        public ReferenceTypeId create(long id, ClassInfo classInfo) {
          // TODO we probably already know what we want thus we should maybe
          // force and wrap as with objectIdManager
          return ReferenceTypeId.factory(id, classInfo);
        }
      });

  private IdManager<FieldId, FieldInfo> fieldIdManager = new IdManager<FieldId, FieldInfo>(new IdFactory<FieldId, FieldInfo>() {
    @Override
    public FieldId create(long id, FieldInfo fieldInfo) {
      return new FieldId(id, fieldInfo);
    }
  });
  private IdManager<FrameId, StackFrame> frameIdManager = new IdManager<FrameId, StackFrame>(new IdFactory<FrameId, StackFrame>() {
    @Override
    public FrameId create(long id, StackFrame stackFrame) {
      return new FrameId(id, stackFrame);
    }
  });

  private JdwpObjectManager() {
  }

  public ArrayId readArrayId(ByteBuffer bytes) throws JdwpError {
    return objectIdManager.readIdentifier(bytes, ArrayId.class);
  }

  public ArrayTypeReferenceId readArrayTypeReferenceId(ByteBuffer bytes) throws JdwpError {
    return (ArrayTypeReferenceId) readReferenceTypeId(bytes);
  }

  public ReferenceTypeId readReferenceTypeId(ByteBuffer bytes) throws JdwpError {
    // TODO throw ErrorType.INVALID_CLASS
    return referenceIdManager.readIdentifier(bytes);
  }

  public ReferenceTypeId getReferenceTypeId(ClassInfo classInfo) {
    return referenceIdManager.getIdentifierId(classInfo);
  }

  /**
   * Gets the {@link Identifier} for the given object that will represent this
   * object in the JPDA.<br/>
   * If the representation doesn't exist yet, new instance of {@link ObjectId}
   * or it's subclasses is created, otherwise and existing identifier is
   * returned.
   * 
   * @param object
   *          The object that needs an ID representation
   * @return The identifier for the given parameter.
   */
  public ObjectId getObjectId(ElementInfo object) {
    if (object == null) {
      return NullObjectId.getInstance();
    }
    return objectIdManager.getIdentifierId(object);
  }

  public FieldId getFieldId(FieldInfo fieldInfo) {
    return fieldIdManager.getIdentifierId(fieldInfo);
  }

  public FrameId getFrameId(StackFrame stackFrame) {
    return frameIdManager.getIdentifierId(stackFrame);
  }

  public FieldId readFieldId(ByteBuffer bytes) {
    return fieldIdManager.readIdentifier(bytes);
  }

  public FrameId readFrameId(ByteBuffer bytes) {
    return frameIdManager.readIdentifier(bytes);
  }

  public ClassObjectId readClassObjectId(ByteBuffer bytes) {
    return objectIdManager.readIdentifier(bytes, ClassObjectId.class);
  }

  public ClassLoaderId readClassLoaderId(ByteBuffer bytes) {
    return objectIdManager.readIdentifier(bytes, ClassLoaderId.class);
  }

  public ThreadId readThreadId(ByteBuffer bytes) {
    // try {
    return objectIdManager.readIdentifier(bytes, ThreadId.class);
    // } catch (InvalidObject io) {
    // // TODO throw invalid thread probably
    // throw new JdwpError(ErrorType.INVALID_THREAD);
    // }
  }

  public ObjectId readObjectId(ByteBuffer bytes) {
    return objectIdManager.readIdentifier(bytes);
  }

  public ClassLoaderId getClassLoaderObjectId(ClassLoaderInfo classLoaderInfo) {
    return objectIdManager.getClassLoaderId(classLoaderInfo);
  }

  public ClassObjectId getClassObjectId(ClassInfo classInfo) {
    return objectIdManager.getClassObjectId(classInfo);
  }

  public ThreadId getThreadId(ThreadInfo threadInfo) {
    return objectIdManager.getThreadId(threadInfo);
  }

  public ArrayId getArrayId(ElementInfo elementInfoArray) {
    return objectIdManager.getIdentifierId(elementInfoArray, ArrayId.class);
  }

  public ThreadGroupId getThreadGroupId(ElementInfo elementThreadGroup) {
    return objectIdManager.getIdentifierId(elementThreadGroup, ThreadGroupId.class);
  }

  public StringId readStringId(ByteBuffer bytes) {
    return objectIdManager.readIdentifier(bytes, StringId.class);
  }

}
