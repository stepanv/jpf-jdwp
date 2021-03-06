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

import gov.nasa.jpf.jdwp.exception.id.InvalidFieldIdException;
import gov.nasa.jpf.jdwp.exception.id.InvalidFrameIdException;
import gov.nasa.jpf.jdwp.exception.id.InvalidIdentifierException;
import gov.nasa.jpf.jdwp.exception.id.InvalidMethodIdException;
import gov.nasa.jpf.jdwp.exception.id.type.InvalidArrayTypeException;
import gov.nasa.jpf.jdwp.exception.id.type.InvalidClassTypeException;
import gov.nasa.jpf.jdwp.exception.id.type.InvalidReferenceTypeException;
import gov.nasa.jpf.jdwp.id.object.ObjectIdManager;
import gov.nasa.jpf.jdwp.id.object.special.NullReferenceId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeIdBase;
import gov.nasa.jpf.vm.*;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

/**
 * <p>
 * JDWP ID manager translates all the IDs sent across the JDWP to the particular
 * VM objects and vice versa.
 * <p>
 * <p>
 * There are still several questions that need an answer:
 * <ol>
 * <li>
 * <h2>Reference IDs</h2>
 * Are {@link ClassInfo} instances (that are translated to reference IDs)
 * guaranteed to not be
 * <ul>
 * <li>reused to represent different class during the state traversal</li>
 * <li>if GCed then no other different created ClassInfo would equal to the GCed
 * one?</li>
 * <li>have always the same <tt>hashCode</tt> during the space traversal (unlike
 * {@link ElementInfo} instances that change it a lot</li>
 * </ul>
 * If not, we have the same problem as with {@link ElementInfo} instances in
 * {@link ObjectIdManager}. Maybe the {@link System#identityHashCode(Object)}
 * would work here.</li>
 * <li>
 * <h2>Field IDs</h2>
 * {@link FieldInfo} subtypes do not override {@link Object#hashCode()} nor
 * {@link Object#equals(Object)} which is good.<br/>
 * The main question is therefore whether JPF reuses {@link FieldInfo} instances
 * for different fields? <br/>
 * The same question could go little bit further and that is whether the
 * lifecycle of {@link FieldInfo} instances is anyhow different from this one:
 * <ol>
 * <li>{@link FieldInfo} is created; That means it is referenced from some
 * existing {@link ClassInfo} that represents some loaded class.</li>
 * <li>This instance of {@link FieldInfo} is never
 * <ul>
 * <li>GCed (if so, we just need to keep a {@link WeakReference} of it so that
 * we can detect such a change)</li>
 * <li>or reused.</li>
 * </ul>
 * If so, we have a problem.</li>
 * </ol>
 * Does JPF by the way support unloading of classes that may consecutively imply
 * GCing of {@link ClassInfo} instances, hence the {@link FieldInfo} instances?<br/>
 * Well, the same question apply for methods as well.<br/>
 * I don't see any <i>unload</i> like methods in {@link ClassLoaderInfo} so
 * maybe these questions are completely irrelevant.</li>
 * <li>
 * <h2>Stack frame ID</h2>
 * Well, we're actually not that fine as I thought.<br/>
 * Even though the frame IDs should be valid only during the time JPF is
 * suspended by the debugger and thus they cannot be reused nor GCed.<br/>
 * The problem is that if I need to modify a value on a stack, I would obtain a
 * modifiable frame in case it's frozen and this new frame is not equal with the
 * old one (ie. the {@link Object#equals(Object)} doesn't return true. So the
 * question is whether there is a better way how to identify a frame than by
 * combining a thread id and the frame's position on a stack.<br/>
 * <span style="text-decoration: line-through;">The only question is whether
 * {@link StackFrame#equals(Object)} method would always return <tt>false</tt>
 * for all other StackFrames that are used by other threads?<br/>
 * I have this bad feeling that this method would return <tt>true</tt> if there
 * are two similar threads executing the same code and stopped at the same
 * instruction.</span></li>
 * <li>
 * <h2>Method IDs</h2>
 * Is it ok to use {@link MethodInfo#getGlobalId()}? That means, is it true that
 * <ul>
 * <li>this gid never changes for a particular method</li>
 * <li>this gid is never the same for two different methods (it would be
 * actually enough if this is true just for methods in one class).</li>
 * </ul>
 * </li>
 * </ol>
 * </p>
 * 
 * @author stepan
 * 
 */
public class JdwpIdManager extends ObjectIdManager {

  /** Lazy load singleton */
  private static class JdwpObjectManagerHolder {
    private static final JdwpIdManager instance = new JdwpIdManager();
  }

  /**
   * Get this singleton instance.
   * 
   * @return
   */
  public static JdwpIdManager getInstance() {
    return JdwpObjectManagerHolder.instance;
  }

  private ReferenceIdManager referenceIdManager = new ReferenceIdManager();
  private FrameIdManager frameIdManager = new FrameIdManager();
  private FieldIdManager fieldIdManager = new FieldIdManager();
  private MethodIdManager methodIdManager = new MethodIdManager();

  /**
   * All Reference IDs manager.<br/>
   * IDs for references have dedicated numbering.
   * 
   * @author stepan
   * 
   */
  private static class ReferenceIdManager extends IdManager<ReferenceTypeId, ClassInfo, InvalidReferenceTypeException> {

    public ReferenceIdManager() {
      super(NullReferenceId.getInstance());
    }

    @Override
    public ReferenceTypeId createIdentifier(Long id, ClassInfo classInfo) {
      return ReferenceTypeIdBase.factory(id, classInfo);
    }

    @Override
    public InvalidReferenceTypeException identifierNotFound(long id) {
      return new InvalidReferenceTypeException(id);
    }

  }

  /**
   * All Field IDs manager.<br/>
   * IDs for fields have dedicated numbering.
   * 
   * @author stepan
   * 
   */
  private static class FieldIdManager extends IdManager<FieldId, FieldInfo, InvalidFieldIdException> {

    @Override
    public FieldId createIdentifier(Long id, FieldInfo field) {
      return new FieldId(id, field);
    }

    @Override
    public InvalidFieldIdException identifierNotFound(long id) {
      return new InvalidFieldIdException(id);
    }

  }

  /**
   * All frame IDs manager.<br/>
   * IDs for frames are computed from the thread object reference id and it's
   * call stack depth.
   * 
   * @author stepan
   * 
   */
  private static class FrameIdManager extends IdManager<FrameId, StackFrame, InvalidFrameIdException> {

    @Override
    public FrameId createIdentifier(Long id, StackFrame stackFrame) {
      throw new UnsupportedOperationException("This method is not supported!");
    }

    @Override
    public InvalidFrameIdException identifierNotFound(long id) {
      return new InvalidFrameIdException(id);
    }

    @Override
    public synchronized FrameId getIdentifierId(StackFrame object) {
      throw new UnsupportedOperationException("This method is not supported!");
    }

    @Override
    public FrameId readIdentifier(ByteBuffer bytes) throws InvalidFrameIdException {
      long id = bytes.getLong();
      return new FrameId(id);
    }

    /**
     * @param threadInfo
     * @param i
     * @return
     */
    public FrameId getIdentifierId(ThreadInfo threadInfo, int i) {
      return new FrameId(threadInfo, i);
    }

  }

  /**
   * All the methods ID manager.</br> This manager relies on
   * {@link MethodInfo#getGlobalId()} global ID that is promised to be unique
   * not only for a {@link ClassInfo} instance which would be just fine but also
   * for the whole Classloader and the classes it loaded.
   * 
   * @author stepan
   * 
   */
  private static class MethodIdManager extends IdManager<MethodId, MethodInfo, InvalidMethodIdException> {

    @Override
    public MethodId createIdentifier(Long id, MethodInfo stackFrame) {
      throw new UnsupportedOperationException("This method is not supported!");
    }

    @Override
    public InvalidMethodIdException identifierNotFound(long id) {
      return new InvalidMethodIdException(id);
    }

    @Override
    public synchronized MethodId getIdentifierId(MethodInfo method) {
      return new MethodId(method);
    }

    /**
     * Reads the identifier from the buffer of bytes and verifies that the
     * method is also declared in the provided class or its supertypes.
     * 
     * @param classInfo
     *          The class.
     * @param bytes
     *          The buffer of bytes.
     * @return The method ID.
     * @throws InvalidMethodIdException
     *           If the given ID is not valid method.
     */
    MethodId readIdentifier(ClassInfo classInfo, ByteBuffer bytes) throws InvalidMethodIdException {
      long id = bytes.getLong();

      MethodId methodId = new MethodId(id);

      if (methodId.get() != methodInfoLookup(classInfo, id)) {
        throw identifierNotFound(id);
      }
      return methodId;
    }

    /**
     * Recursively looks up a method in the given class and its supertypes.
     * 
     * @param classInfo
     *          The class.
     * @param id
     *          The ID of the method.
     * @return The method info.
     * @throws InvalidMethodIdException
     *           If the given ID doesn't stand for a method or the method is not
     *           declared by the provided class or its supertypes.
     */
    private static MethodInfo methodInfoLookup(ClassInfo classInfo, long id) throws InvalidMethodIdException {
      logger.debug("looking for METHOD global id: {} of CLASS: {}", id, classInfo);
      for (MethodInfo methodInfo : classInfo.getDeclaredMethodInfos()) {
        if (id == methodInfo.getGlobalId()) {
          logger.trace("METHOD found: {}", methodInfo);
          return methodInfo;
        }
      }
      // also try super types
      if (classInfo.getSuperClass() != null) {
        return methodInfoLookup(classInfo.getSuperClass(), id);
      }
      throw new InvalidMethodIdException(new MethodId(id));
    }

  }

  /** This class is a singleton */
  private JdwpIdManager() {
  }

  /*
   * REFERENCE TYPE related methods
   */

  /**
   * Reads Reference ID from the given buffer of bytes.<br/>
   * Note that this may also return any subtype reference ID.
   * 
   * @param bytes
   *          The buffer of bytes to read the reference ID from.
   * @return The corresponding reference ID.
   * @throws InvalidReferenceTypeException
   *           If the given ID is not a known reference ID.
   */
  public ReferenceTypeId readReferenceTypeId(ByteBuffer bytes) throws InvalidReferenceTypeException {
    return referenceIdManager.readIdentifier(bytes);
  }

  /**
   * Reads Array Reference ID from the given buffer of bytes.
   * 
   * @param bytes
   *          The buffer of bytes to read the array reference ID from.
   * @return The corresponding reference ID.
   * @throws InvalidArrayTypeException
   *           If the given ID is not a valid array reference ID.
   * @throws InvalidReferenceTypeException
   *           If the given ID is not a known reference ID.
   */
  public ReferenceTypeId readArrayTypeReferenceId(ByteBuffer bytes) throws InvalidArrayTypeException, InvalidReferenceTypeException {
    ReferenceTypeId refId = readReferenceTypeId(bytes);
    if (!refId.isArrayType()) {
      throw new InvalidArrayTypeException(refId);
    }
    return refId;
  }

  /**
   * Reads Class Reference ID from the given buffer of bytes.
   * 
   * @param bytes
   *          The buffer of bytes to read the class reference ID from.
   * @return The corresponding reference ID.
   * @throws InvalidClassTypeException
   *           If the given ID is not a valid class reference ID.
   * @throws InvalidReferenceTypeException
   *           If the given ID is not a known reference ID.
   */
  public ReferenceTypeId readClassTypeId(ByteBuffer bytes) throws InvalidClassTypeException, InvalidReferenceTypeException {
    ReferenceTypeId refId = readReferenceTypeId(bytes);
    if (!refId.isClassType()) {
      throw new InvalidClassTypeException(refId);
    }
    return refId;
  }

  /**
   * Reads Interface Reference ID from the given buffer of bytes.
   * 
   * @param bytes
   *          The buffer of bytes to read the interface reference ID from.
   * @return The corresponding reference ID.
   * @throws InvalidReferenceTypeException
   *           If the given ID is not a valid class reference ID or if the given
   *           ID is not a known reference ID.
   */
  public ReferenceTypeId readInterfaceTypeId(ByteBuffer bytes) throws InvalidReferenceTypeException {
    ReferenceTypeId refId = readReferenceTypeId(bytes);
    if (!refId.isInterfaceType()) {
      throw new InvalidReferenceTypeException(refId);
    }
    return refId;
  }

  /**
   * Gets the reference ID for given class.<br/>
   * Creates new reference ID or an existing one provided a reference ID was
   * already created.
   * 
   * @param classInfo
   *          The representation of the class.
   * @return The reference ID of the given class.
   */
  public ReferenceTypeId getReferenceTypeId(ClassInfo classInfo) {
    return referenceIdManager.getIdentifierId(classInfo);
  }

  /*
   * OTHER VM OBJECTS related methods
   */

  /**
   * Gets or creates an ID for the given field.
   * 
   * @param fieldInfo
   *          The field.
   * @return The ID that represents the given field.
   */
  public FieldId getFieldId(FieldInfo fieldInfo) {
    return fieldIdManager.getIdentifierId(fieldInfo);
  }

  /**
   * Gets or creates an ID for a frame at the call stack depth of the given
   * thread.
   * 
   * @param thread
   *          The thread of which the frame is looked for.
   * @param depth
   *          The call stack depth where to get the frame at.
   * @return The ID that represents the given frame.
   */
  public FrameId getFrameId(ThreadInfo thread, int depth) {
    return frameIdManager.getIdentifierId(thread, depth);
  }

  /**
   * Gets or creates an ID for the given method.
   * 
   * @param methodInfo
   *          The method.
   * @return The ID that represents the given method.
   */
  public MethodId getMethodId(MethodInfo methodInfo) {
    return methodIdManager.getIdentifierId(methodInfo);
  }

  /**
   * Reads a field ID from the given buffer of bytes.
   * 
   * @param bytes
   *          The buffer of bytes to read the ID from.
   * @return The field ID.
   * @throws InvalidIdentifierException
   *           If given ID is not a valid ID of a field.
   */
  public FieldId readFieldId(ByteBuffer bytes) throws InvalidIdentifierException {
    return fieldIdManager.readIdentifier(bytes);
  }

  /**
   * Reads a frame ID from the given buffer of bytes.
   * 
   * @param bytes
   *          The buffer of bytes to read the ID from.
   * @return The frame ID.
   * @throws InvalidIdentifierException
   *           If given ID is not a valid ID of a frame.
   */
  public FrameId readFrameId(ByteBuffer bytes) throws InvalidIdentifierException {
    return frameIdManager.readIdentifier(bytes);
  }

  /**
   * Reads a method ID from the given buffer of bytes.
   * 
   * @param bytes
   *          The buffer of bytes to read the ID from.
   * @return The method ID.
   * @throws InvalidIdentifierException
   *           If given ID is not a valid ID of a method.
   */
  public MethodId readMethodId(ClassInfo classInfo, ByteBuffer bytes) throws InvalidIdentifierException {
    return methodIdManager.readIdentifier(classInfo, bytes);
  }

}
