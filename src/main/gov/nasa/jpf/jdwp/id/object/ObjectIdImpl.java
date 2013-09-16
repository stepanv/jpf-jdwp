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

package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.command.ObjectReferenceCommand;
import gov.nasa.jpf.jdwp.exception.JdwpException.ErrorType;
import gov.nasa.jpf.jdwp.exception.id.InvalidIdentifierException;
import gov.nasa.jpf.jdwp.exception.id.object.InvalidObjectException;
import gov.nasa.jpf.jdwp.id.Identifier;
import gov.nasa.jpf.jdwp.id.JdwpIdManager;
import gov.nasa.jpf.jdwp.id.TaggableIdentifier;
import gov.nasa.jpf.jdwp.id.TaggableIdentifierBase;
import gov.nasa.jpf.jdwp.id.object.special.NullObjectId;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.DynamicElementInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Heap;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class implements the corresponding <code>objectID</code> common data
 * type (tagged-objectID respectively) from the JDWP Specification.
 * 
 * <p>
 * {@link ObjectId} class stands for all the elements in the JPF that are
 * accessible from the SUT.<br/>
 * The {@link ElementInfo} generic type of {@link TaggableIdentifier} forces all
 * instances of classes and subclasses of {@link ObjectId} to represent an
 * {@link ElementInfo}.<br/>
 * There are several subclasses of this class that represent only specific
 * objects in SUT (like {@link ThreadId} represents {@link Thread} which is
 * represented by {@link ThreadInfo}) which are required by the JDWP
 * Specification. Nevertheless, those subclasses are sometimes treated by JPDA
 * as {@link ObjectId} instances as well.
 * 
 * <br/>
 * 
 * <h3>ElementInfo hashCode invariant problem</h3>
 * The biggest problem with ElementInfos is that their hashCode() method returns
 * different values throughout the lifetime of the object they represent.<br/>
 * If ElementInfo represents java.lang.Thread then its hashCode changes even
 * when the thread changes its state from STARTED to RUNNING.<br/>
 * Therefore it's not possible to put ElementInfos into hashMaps and it's also
 * tricky to call equals (since equals is congruent).
 * 
 * <br/>
 * 
 * <h3>What kind of information do we need</h3>
 * Every ObjectId stands for one object instance which is identified by its
 * pointer (heap obj ref).<br/>
 * JPF is little bit tricky since it can recreate new ElementInfo instance for a
 * object instance in SuT. And therefore it's irrelevant to keep the ElementInfo
 * instance here (even as a weak reference). We should keep the HEAP index and
 * always return the up-to-date ElementInfo instance.<br/>
 * The only question is what if HEAP index is reused after the GC activity by
 * completely strange new object?<br/>
 * TODO [for PJA] is this possible?
 * 
 * <br/>
 * 
 * <h2>JDWP Specification</h2>
 * Uniquely identifies an object in the target VM. A particular object will be
 * identified by exactly one objectID in JDWP commands and replies throughout
 * its lifetime (or until the objectID is explicitly disposed). An ObjectID is
 * not reused to identify a different object unless it has been explicitly
 * disposed, regardless of whether the referenced object has been garbage
 * collected. An objectID of 0 represents a null object.<br/>
 * 
 * Note that the existence of an object ID does not prevent the garbage
 * collection of the object. Any attempt to access a a garbage collected object
 * with its object ID will result in the {@link ErrorType#INVALID_OBJECT} error
 * code. Garbage collection can be disabled with the
 * {@link ObjectReferenceCommand#DISABLECOLLECTION} command, but it is not
 * usually necessary to do so.
 * </p>
 * 
 * @author stepan
 * 
 */
public class ObjectIdImpl extends TaggableIdentifierBase<DynamicElementInfo> implements ObjectId {

  private Tag tag;

  private int objectRef;

  /**
   * Constructs the {@link ObjectIdImpl} as a part of a subclass.
   * 
   * @param tag
   *          The tag of the object this taggable identifier stands for.
   * @param id
   *          The ID.
   * @param objectRef
   *          The heap object reference.
   * @param object
   *          The object this taggable identifier represents.
   */
  protected ObjectIdImpl(Tag tag, long id, ElementInfo object) {
    this(tag, id, object.getObjectRef());
  }

  /**
   * Constructs the {@link ObjectIdImpl} as a part of a subclass.
   * 
   * @param tag
   *          The tag of the object this taggable identifier stands for.
   * @param id
   *          The ID.
   * @param objectRef
   *          The heap object reference.
   */
  protected ObjectIdImpl(Tag tag, long id, int objectRef) {
    super(id, null);
    this.tag = tag;
    this.objectRef = objectRef;
  }

  @Override
  public Tag getIdentifier() {
    return tag;
  }

  @Override
  public void push(StackFrame frame) {
    frame.pushRef(objectRef);
  }

  @Override
  public void modify(StackFrame stackFrame, int slotIndex) {
    stackFrame.setLocalVariable(slotIndex, objectRef, true);
  }

  @Override
  public void modify(ElementInfo instance, FieldInfo field) {
    instance.setReferenceField(field, objectRef);
  }

  @Override
  public void modify(ElementInfo arrayInstance, int index) {
    arrayInstance.setReferenceElement(index, objectRef);
  }

  @Override
  public DynamicElementInfo get() throws InvalidObjectException {
    Heap heap = VM.getVM().getHeap();
    return (DynamicElementInfo) heap.get(objectRef);
  }

  @Override
  public void disableCollection() throws InvalidObjectException {
    Heap heap = VM.getVM().getHeap();
    heap.registerPinDown(objectRef);
  }

  @Override
  public void enableCollection() throws InvalidObjectException {
    Heap heap = VM.getVM().getHeap();
    heap.releasePinDown(objectRef);
  }

  @Override
  public boolean isNull() {
    Heap heap = VM.getVM().getHeap();
    return heap.get(objectRef) == null;
  }

  /**
   * Factory that creates JDWP object identifier for the given parameter.<br/>
   * Note that this factory should be used only and only if the given object
   * doesn't have a JDWP identifier yet (an instance of {@link ObjectId} or it's
   * subclasses).
   * 
   * @see JdwpIdManager#getObjectId(ElementInfo)
   * 
   * @param id
   *          Unique id that is used in the JDWP protocol to represent the given
   *          object
   * @param object
   *          The object to be represented by the result of this factory
   * @return The {@link Identifier} instance of the given object
   */
  static ObjectId objectIdFactory(long id, ElementInfo object) {
    ClassInfo classInfo = object.getClassInfo();

    /*
     * Here, we need to dynamically find whether the object is more than just a
     * normal object. It is important to understand, that methods like
     * classInfo.isThreadClassInfo() are misleading since we can have also
     * subclasses of standard java.lang classes.
     */

    if (classInfo.isArray()) {
      return new ArrayIdImpl(id, object);
    } else if (classInfo.isInstanceOf("java.lang.Thread")) {
      return new ThreadIdImpl(id, object);
    } else if (classInfo.isInstanceOf("java.lang.String")) {
      return new StringIdImpl(id, object);
    } else if (classInfo.isInstanceOf("java.lang.Class")) {
      return new ClassObjectIdImpl(id, object);
    } else if (classInfo.isInstanceOf("java.lang.ThreadGroup")) {
      return new ThreadGroupIdImpl(id, object);
    } else if (classInfo.isInstanceOf("java.lang.ClassLoader")) {
      return new ClassLoaderIdImpl(id, object);
    } else {
      // any other ElementInfos don't have a specific representation in
      // the JDWP Specification
      return new ObjectIdImpl(Tag.OBJECT, id, object);
    }
  }

  @Override
  public void writeUntagged(DataOutputStream os) throws IOException {
    write(os);
  }

  @Override
  public DynamicElementInfo getModifiable() throws InvalidObjectException {
    Heap heap = VM.getVM().getHeap();
    return (DynamicElementInfo) heap.getModifiable(objectRef);
  }

  @Override
  public DynamicElementInfo nullObjectHandler() throws InvalidIdentifierException {
    return NullObjectId.getInstance().get();
  }
}
