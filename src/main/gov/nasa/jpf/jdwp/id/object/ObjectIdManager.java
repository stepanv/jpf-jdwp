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

import gov.nasa.jpf.jdwp.exception.id.object.InvalidArrayException;
import gov.nasa.jpf.jdwp.exception.id.object.InvalidClassLoaderException;
import gov.nasa.jpf.jdwp.exception.id.object.InvalidClassObjectException;
import gov.nasa.jpf.jdwp.exception.id.object.InvalidObjectException;
import gov.nasa.jpf.jdwp.exception.id.object.InvalidStringException;
import gov.nasa.jpf.jdwp.exception.id.object.InvalidThreadException;
import gov.nasa.jpf.jdwp.exception.id.object.InvalidThreadGroupException;
import gov.nasa.jpf.jdwp.id.Identifier;
import gov.nasa.jpf.jdwp.id.IdentifierPointer;
import gov.nasa.jpf.jdwp.id.object.special.NullObjectId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.DynamicElementInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Heap;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * The purpose of ObjectId manager class is to manage all <i>SuT</i> instances,
 * that is all {@link DynamicElementInfo} instances on the JPF side.<br/>
 * The majority of these instances are mirrored with {@link ObjectId} but some
 * special classes are associated with more special ones like {@link ThreadId}
 * or {@link ClassLoaderId}.
 * </p>
 * <p>
 * Right now, it's not really clear whether current implementation really works
 * with the way how JPF manages {@link ElementInfo} objects.<br/>
 * There are several aspects that need to be further approved by JPF
 * specialists.
 * <ol>
 * <li>
 * <h3>JDWP ID reuse</h3>
 * The IDs that are used across JDWP cannot be reused for different objects
 * unless they are explicitly disposed.<br/>
 * Is that true for <tt>objRef</tt> integers that are used as an index in
 * {@link Heap} instances?<br/>
 * If not, or if it cannot be guaranteed, dedicated IDs have to be used by the
 * JDWP back-end.</li>
 * <li>
 * <h3>ElementInfo equality</h3>
 * The {@link ElementInfo} class overrides {@link Object#equals(Object)} (as
 * well as {@link Object#hashCode()} which is a reason why {@link ElementInfo}
 * instances cannot be put into {@link Map} objects). The question is how can we
 * really know whether two different {@link ElementInfo} instances represent the
 * same objects from the debugger perspective? As far as I understand the
 * purpose of this overridden <tt>equals</tt> method it is for the purpose of
 * program state comparison so that when this method returns <tt>false</tt> it
 * means two program states are different.<br/>
 * But here, in JDWP, we don't care about different states, we need to track the
 * {@link ElementInfo} instances with the same ID throughout it's lifetime
 * regardless whether JPF is going forward during space traversal where a
 * particular object can be
 * <ul>
 * <li>created</li>
 * <li>and then possibly collected</li>
 * <li>and then a new {@link ElementInfo} may reuse the same <tt>objRef</tt></li>
 * </ul>
 * or whether JPF is backtracking and may
 * <ul>
 * <li>recreate the same object from the debugger perspective with the same
 * <tt>objRef</tt> or different one</li>
 * <li>or may create completely new object but to reuse the <tt>objRef</tt> from
 * a different {@link ElementInfo} that is not used in this state</li>
 * </ul>
 * So the question is whether <tt>objRef</tt> can be used for object comparison
 * or not?<br/>
 * If not, maybe pseudo-unique <i>System hashCodes</i> (See
 * {@link System#identityHashCode(Object)}) may be used for {@link ElementInfo}
 * comparison provided that {@link ElementInfo} instances are never collected by
 * JVM (see the next number bellow).</li>
 * 
 * <li>
 * <h3>JVM garbage collection of {@link ElementInfo} instances</h3>
 * Does JPF keep all ElementInfo references forever so that the GC never release
 * them?<br/>
 * If so, it means that
 * <ul>
 * <li>JDWP doesn't have to use {@link Reference} to {@link ElementInfo} since
 * they're not going to be GCed anyway.</li>
 * <li>How is it reflected in the JPF storage of {@link ElementInfo} instance
 * when a particular object in SuT is GCed. Does it mean the {@link ElementInfo}
 * of this object can be GCed too? If not, how may JDWP determine whether the
 * {@link ElementInfo} represents an object that is GCed?</li>
 * <li><i>System IDs</i> such as {@link System#identityHashCode(Object)} can be
 * used for {@link ElementInfo} comparison.<br/>
 * It would be better though to not use hashes but does even JVM expose better
 * object identification?</li>
 * </ul>
 * All this would not work if JPF reuses {@link ElementInfo} objects for
 * different SuT objects.</li>
 * <li>
 * <h3></h3></li>
 * <li></li>
 * <li></li>
 * </ol>
 * 
 * @author stepan
 * 
 */
public class ObjectIdManager {

  final static Logger logger = LoggerFactory.getLogger(ObjectIdManager.class);

  /**
   * A set of Object ID Identifiers that are used in this JDWP session.<br/>
   * This map shouldn't cause {@link OutOfMemoryError} error if it grows too
   * much. If it does though the JDWP back-end will start to produce
   * {@link InvalidObjectException} errors to the debugger.
   */
  private Map<ObjectId, SoftReference<ObjectId>> idMap = new WeakHashMap<>();

  /** This class can be subclassed only */
  protected ObjectIdManager() {
    idMap.put(NullObjectId.getInstance(), new SoftReference<ObjectId>(NullObjectId.getInstance()));
  }

  private <I extends ObjectId> ObjectId get(IdentifierPointer pointer) throws InvalidObjectException {
    Reference<? extends ObjectId> ref = idMap.get(pointer);
    if (ref == null) {
      // there is no such ID registered by this ID manager
      identifierDiscarded(pointer.getId());
    }
    ObjectId identifier = ref.get();
    if (identifier == null) {
      // this may happen only if <tt>identifier</tt> was just discarded between
      // the <tt>get</tt> call from the hashmap
      // and the get call of the reference
      if (idMap.get(pointer) == null) {
        // it really happened! The object collection really happened between the
        // two <tt>get</tt> calls above
        identifierDiscarded(pointer.getId());
      } else {
        // this should not ever happen... see the theory behind explanation in
        // this file
        throw new IllegalStateException("The idea behind this manager prooved to be wrong! This is very very bad!");
      }
    }
    return identifier;
  }

  private void identifierDiscarded(long id) throws InvalidObjectException {
    throw new InvalidObjectException(id);
  }

  /**
   * This class is for creation of specific {@link ObjectId} and it's subtypes
   * instances.<br/>
   * The idea behind this class is that it automatically does all the necessary
   * object casts and it knows how to recover or what kind of exception should
   * be thrown.
   * 
   * @author stepan
   * 
   * @param <I>
   *          A specific {@link ObjectId} subtype of which this factory creates
   *          identifiers for.
   * @param <E>
   *          A specific {@link InvalidObjectException} subtype that is thrown
   *          if some JDWP ID is not compatible with this factory.
   */
  private abstract class IdFactory<I extends ObjectId, E extends InvalidObjectException> {

    private Class<I> clazz;

    public IdFactory(Class<I> clazz) {
      this.clazz = clazz;
    }

    /**
     * Creates {@link Identifier} with given ID for the given object.
     * 
     * @param id
     *          The ID that will be used for the JDWP communication between a
     *          debugger and a debuggee.
     * @param object
     *          The object that the {@link Identifier} has to be created for.
     * @return a newly created identifier
     */
    public abstract I createIdentifier(long id, ElementInfo object);

    /**
     * The object ID is not compatible with this factory.
     * 
     * @param objectId
     *          The object ID that this factory received from the underlying
     *          manager but is incompatible
     * @return An exception that represents the incompatibility fact
     */
    public abstract E identifierIncompatible(ObjectId objectId);

    /**
     * Get an identifier for a specific object.<br/>
     * It is guaranteed that for the same object the Identifier with the same ID
     * is returned.
     * 
     * @param object
     *          An object to get the identifier for.
     * @return A particular identifier whose ID will be bound with this object
     *         forever.
     */
    public I getIdentifier(ElementInfo object) {
      
      ObjectId objectId;
      
      if (object == null) {
        objectId = NullObjectId.getInstance();
      } else if (!(object instanceof DynamicElementInfo)) {
        throw new IllegalStateException("We have StaticElementInfo instead of DynamicElementInfo! Object: " + object);
      } else {
        
        IdentifierPointer pointer = new IdentifierPointer((long) object.getObjectRef());
  
        if (idMap.containsKey(pointer)) {
          // this ID is already managed
  
          try {
            objectId = get(pointer);
          } catch (InvalidObjectException e) {
            // the table contains this ID; however, an exception occurred which
            // should not happened
            // this is probably programmer's fault
            throw new IllegalStateException("The Object ID manager contains incompatible identifier for object: '" + object + "'", e);
          }
  
          if (!object.equals(objectId.get())) {
            // this exception proves that we cannot compare element infos ...
            // if this happens a redesign is required
            throw new IllegalStateException(String.format("Object '%s' is not object '%s' for objectId '%s'", object, objectId.get(),
                                                          objectId));
          }
  
        } else {
          objectId = createIdentifier(pointer.getId(), object);
          idMap.put(objectId, new SoftReference<ObjectId>(objectId));
  
          logger.debug("Created object ID: {}, (identifier: {}) object: {}, class: {}, classInfo: {}", pointer.getId(), objectId, object,
                       object.getClass(), ((ElementInfo) object).getClassInfo());
        }
      
      }
      
      if (clazz.isInstance(objectId)) {
        return clazz.cast(objectId);
      }

      // this should be a dead code since the same thing is checked already by
      // the get() method
      throw new IllegalStateException("Object: '" + object + "' has associated '" + objectId + "' which cannot be cast to '" + clazz + "'");

    }

    /**
     * Lookup the given ID in the underlying Object ID manager's table.
     * 
     * @param id
     *          The JDWP ID to look for
     * @return The identifier for this ID
     * @throws E
     *           If the given ID doesn't represent <tt>I</tt> identifier.
     * @throws InvalidObjectException
     *           If the identifier of the given ID is not compatible with this
     *           factory.
     */
    public I lookupObjectId(long id) throws E, InvalidObjectException {
      ObjectId objectId = get(new IdentifierPointer(id));

      if (clazz.isInstance(objectId)) {
        return clazz.cast(objectId);
      }

      throw identifierIncompatible(objectId);
    }

  }

  /*
   * The FACTORIES
   */

  /**
   * Factory for {@link ObjectId} instances lookup and creation.
   */
  private IdFactory<ObjectId, InvalidObjectException> defaultIdFactory = new IdFactory<ObjectId, InvalidObjectException>(ObjectId.class) {
    @Override
    public ObjectId createIdentifier(long id, ElementInfo object) {
      return ObjectIdImpl.objectIdFactory(id, object);
    }

    @Override
    public InvalidObjectException identifierIncompatible(ObjectId objectId) {
      return new InvalidObjectException(objectId);
    }

  };

  /**
   * Factory for {@link ClassLoaderId} instances lookup and creation.
   */
  private IdFactory<ClassLoaderId, InvalidClassLoaderException> classLoaderIdFactory = new IdFactory<ClassLoaderId, InvalidClassLoaderException>(
      ClassLoaderId.class) {
    @Override
    public ClassLoaderId createIdentifier(long id, ElementInfo object) {
      return new ClassLoaderIdImpl(id, object);
    }

    @Override
    public InvalidClassLoaderException identifierIncompatible(ObjectId objectId) {
      return new InvalidClassLoaderException(objectId);
    }
  };

  /**
   * Factory for {@link ClassObjectId} instances lookup and creation.
   */
  private IdFactory<ClassObjectId, InvalidClassObjectException> classObjectIdFactory = new IdFactory<ClassObjectId, InvalidClassObjectException>(
      ClassObjectId.class) {
    @Override
    public ClassObjectId createIdentifier(long id, ElementInfo object) {
      return new ClassObjectIdImpl(id, object);
    }

    @Override
    public InvalidClassObjectException identifierIncompatible(ObjectId objectId) {
      return new InvalidClassObjectException(objectId);
    }
  };

  /**
   * Factory for {@link ThreadId} instances lookup and creation.
   */
  private IdFactory<ThreadId, InvalidThreadException> threadIdFactory = new IdFactory<ThreadId, InvalidThreadException>(ThreadId.class) {
    @Override
    public ThreadId createIdentifier(long id, ElementInfo object) {
      return new ThreadIdImpl(id, object);
    }

    @Override
    public InvalidThreadException identifierIncompatible(ObjectId objectId) {
      return new InvalidThreadException(objectId);
    }
  };

  /**
   * Factory for {@link ThreadGroupId} instances lookup and creation.
   */
  private IdFactory<ThreadGroupId, InvalidThreadGroupException> threadGroupIdFactory = new IdFactory<ThreadGroupId, InvalidThreadGroupException>(
      ThreadGroupId.class) {
    @Override
    public ThreadGroupId createIdentifier(long id, ElementInfo object) {
      return new ThreadGroupIdImpl(id, object);
    }

    @Override
    public InvalidThreadGroupException identifierIncompatible(ObjectId objectId) {
      return new InvalidThreadGroupException(objectId);
    }
  };

  /**
   * Factory for {@link ArrayId} instances lookup and creation.
   */
  private IdFactory<ArrayId, InvalidArrayException> arrayIdFactory = new IdFactory<ArrayId, InvalidArrayException>(ArrayId.class) {
    @Override
    public ArrayId createIdentifier(long id, ElementInfo object) {
      return new ArrayIdImpl(id, object);
    }

    @Override
    public InvalidArrayException identifierIncompatible(ObjectId objectId) {
      return new InvalidArrayException(objectId);
    }
  };

  /**
   * Factory for {@link StringId} instances lookup and creation.
   */
  private IdFactory<StringId, InvalidStringException> stringIdFactory = new IdFactory<StringId, InvalidStringException>(StringId.class) {
    @Override
    public StringId createIdentifier(long id, ElementInfo object) {
      return new StringIdImpl(id, object);
    }

    @Override
    public InvalidStringException identifierIncompatible(ObjectId objectId) {
      return new InvalidStringException(objectId);
    }
  };

  /*
   * READ related methods
   */

  /**
   * Reads an {@link ObjectId} from the given buffer.<br/>
   * 
   * @param bytes
   *          A buffer.
   * @return An instance of {@link ObjectId} or any of it's subtypes depending
   *         on what ID was passed by the buffer.
   * @throws InvalidObjectException
   *           If the given ID doesn't represent an object
   */
  public ObjectId readObjectId(ByteBuffer bytes) throws InvalidObjectException {
    return defaultIdFactory.lookupObjectId(bytes.getLong());
  }

  /**
   * Reads a {@link ThreadId} from the given buffer.<br/>
   * 
   * @param bytes
   *          A buffer.
   * @return An instance of {@link ThreadId}.
   * @throws InvalidThreadException
   *           If the given ID doesn't represent {@link ThreadId}.
   * @throws InvalidObjectException
   *           If the given ID was garbage collected or doesn't represent an
   *           object.
   */
  public ThreadId readThreadId(ByteBuffer bytes) throws InvalidThreadException, InvalidObjectException {
    return threadIdFactory.lookupObjectId(bytes.getLong());
  }

  /**
   * Reads a {@link ThreadGroupId} from the given buffer.<br/>
   * 
   * @param bytes
   *          A buffer.
   * @return An instance of {@link ThreadGroupId}.
   * @throws InvalidThreadGroupException
   *           If the given ID doesn't represent {@link ThreadGroupId}.
   * @throws InvalidObjectException
   *           If the given ID was garbage collected or doesn't represent an
   *           object.
   */
  public ThreadGroupId readThreadGroupId(ByteBuffer bytes) throws InvalidThreadGroupException, InvalidObjectException {
    return threadGroupIdFactory.lookupObjectId(bytes.getLong());
  }

  /**
   * Reads an {@link ClassObjectId} from the given buffer.<br/>
   * 
   * @param bytes
   *          A buffer.
   * @return An instance of {@link ClassObjectId}.
   * @throws InvalidClassObjectException
   *           If the given ID doesn't represent {@link ClassObjectId}.
   * @throws InvalidObjectException
   *           If the given ID was garbage collected or doesn't represent an
   *           object.
   */
  public ClassObjectId readClassObjectId(ByteBuffer bytes) throws InvalidClassObjectException, InvalidObjectException {
    return classObjectIdFactory.lookupObjectId(bytes.getLong());
  }

  /**
   * Reads an {@link ClassLoaderId} from the given buffer.<br/>
   * 
   * @param bytes
   *          A buffer.
   * @return An instance of {@link ClassLoaderId}.
   * @throws InvalidClassLoaderException
   *           If the given ID doesn't represent {@link ClassLoaderId}.
   * @throws InvalidObjectException
   *           If the given ID was garbage collected or doesn't represent an
   *           object.
   */
  public ClassLoaderId readClassLoaderId(ByteBuffer bytes) throws InvalidClassLoaderException, InvalidObjectException {
    return classLoaderIdFactory.lookupObjectId(bytes.getLong());
  }

  /**
   * Reads an {@link ArrayId} from the given buffer.<br/>
   * 
   * @param bytes
   *          A buffer.
   * @return An instance of {@link ArrayId}.
   * @throws InvalidArrayException
   *           If the given ID doesn't represent {@link ArrayId}.
   * @throws InvalidObjectException
   *           If the given ID was garbage collected or doesn't represent an
   *           object.
   */
  public ArrayId readArrayId(ByteBuffer bytes) throws InvalidArrayException, InvalidObjectException {
    return arrayIdFactory.lookupObjectId(bytes.getLong());
  }

  /**
   * Reads an {@link StringId} from the given buffer.<br/>
   * 
   * @param bytes
   *          A buffer.
   * @return An instance of {@link StringId}.
   * @throws InvalidStringException
   *           If the given ID doesn't represent {@link StringId}.
   * @throws InvalidObjectException
   *           If the given ID was garbage collected or doesn't represent an
   *           object.
   */
  public StringId readStringId(ByteBuffer bytes) throws InvalidStringException, InvalidObjectException {
    return stringIdFactory.lookupObjectId(bytes.getLong());
  }

  /*
   * Object ID and it's subtypes getters
   */

  /**
   * Gets the {@link Identifier} for the given object that will represent this
   * object in the JPDA.<br/>
   * If the representation doesn't exist yet, new instance of {@link ObjectId}
   * or it's subclasses is created, otherwise and existing identifier is
   * returned.
   * <p>
   * Note that this <i>getter</i> can be used for any JPF objects although it
   * requires little bit more of overhead if this method is used since it must
   * be properly determined whether the parameter determines standard object or
   * some special one like a thread or a classloader (see other <i>get</i>
   * methods here). Apparently, if no assumptions about the specialization of
   * the object can be made, there is no other better method to use than this
   * one.
   * 
   * @param object
   *          The object that needs an ID representation
   * @return The identifier for the given object.
   */
  public ObjectId getObjectId(ElementInfo object) {
    return defaultIdFactory.getIdentifier(object);
  }

  /**
   * Gets the {@link Identifier} for the given class loader that will represent
   * this object in the JPDA.<br/>
   * If the representation doesn't exist yet, new instance of
   * {@link ClassLoaderId} is created, otherwise and existing identifier is
   * returned.
   * 
   * @param classLoaderInfo
   *          The class loader that needs an ID representation
   * @return The identifier for the given class loader.
   */
  public ClassLoaderId getClassLoaderId(ClassLoaderInfo classLoaderInfo) {
    ElementInfo classLoaderObject = VM.getVM().getHeap().get(classLoaderInfo.getClassLoaderObjectRef());
    return classLoaderIdFactory.getIdentifier(classLoaderObject);
  }

  /**
   * Gets the {@link Identifier} for the given class object that will represent
   * this object in the JPDA.<br/>
   * If the representation doesn't exist yet, new instance of
   * {@link ClassObjectId} is created, otherwise and existing identifier is
   * returned.
   * <p>
   * Note that {@link ClassInfo} may be associated with instances and subtypes
   * of {@link ReferenceTypeId} but as the JDWP specification states it should
   * not be assumed that the ID will be the same.<br/>
   * Refer to the definition of <tt>referenceTypeID</tt> in the <i>Detailed
   * Command information table</i>.
   * </p>
   * 
   * @param classInfo
   *          The class object that needs an ID representation
   * @return The identifier for the given class object.
   */
  public ClassObjectId getClassObjectId(ClassInfo classInfo) {
    return classObjectIdFactory.getIdentifier(classInfo.getClassObject());
  }

  /**
   * Gets the {@link Identifier} for the given thread that will represent this
   * object in the JPDA.<br/>
   * If the representation doesn't exist yet, new instance of {@link ThreadId}
   * is created, otherwise and existing identifier is returned.
   * 
   * @param threadInfo
   *          The thread that needs an ID representation
   * @return The identifier for the given thread.
   */
  public ThreadId getThreadId(ThreadInfo threadInfo) {
    return threadIdFactory.getIdentifier(threadInfo.getThreadObject());
  }

  /**
   * Gets the {@link Identifier} for the given thread that will represent this
   * object in the JPDA.<br/>
   * If the representation doesn't exist yet, new instance of {@link ThreadId}
   * is created, otherwise and existing identifier is returned.
   * 
   * @param elementInfo
   *          The array that needs an ID representation
   * @return The identifier for the given array.
   */
  public ArrayId getArrayId(ElementInfo elementInfo) {
    if (!elementInfo.isArray()) {
      throw new IllegalStateException("Given element info '" + elementInfo + "' is not an array!");
    }
    return arrayIdFactory.getIdentifier(elementInfo);
  }

  /**
   * Gets the {@link Identifier} for the given thread that will represent this
   * object in the JPDA.<br/>
   * If the representation doesn't exist yet, new instance of
   * {@link ThreadGroupId} is created, otherwise and existing identifier is
   * returned.
   * 
   * @param elementInfo
   *          The thread group that needs an ID representation
   * @return The identifier for the given thread group.
   */
  public ThreadGroupId getThreadGroupId(ElementInfo elementInfo) {
    if (!elementInfo.getClassInfo().isInstanceOf("java.lang.ThreadGroup")) {
      throw new IllegalStateException("Given element info '" + elementInfo + "' is not a thread group!");
    }
    return threadGroupIdFactory.getIdentifier(elementInfo);
  }

}
