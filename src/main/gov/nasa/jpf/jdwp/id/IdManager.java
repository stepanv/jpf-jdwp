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

import gov.nasa.jpf.jdwp.exception.id.InvalidIdentifierException;
import gov.nasa.jpf.vm.ElementInfo;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class provides a simple infrastructure for managing IDs of particular
 * type of objects from the VM.
 * </p>
 * <p>
 * This manager holds an identifier for some object from the VM as long as the
 * object itself is not collected.<br/>
 * It guaranteed that this manager doesn't block such a managed object from
 * being collected.<br/>
 * It is also guaranteed that this manager doesn't keep a particular identifier
 * of an object if this object was collected and thus this identifier is
 * collected as well.
 * </p>
 * 
 * <p>
 * This class is intended to be thread safe.
 * </p>
 * 
 * @author stepan
 * 
 * @param <I>
 *          The family of identifiers that will represent the family of VM
 *          objects this manager is created for.
 * @param <T>
 *          The family of VM objects this ID manager will manage IDs for.
 * @param <E>
 *          An exception that is thrown if no identifier exists for given ID.
 */
public abstract class IdManager<I extends Identifier<T>, T, E extends InvalidIdentifierException> {

  final static Logger logger = LoggerFactory.getLogger(IdManager.class);

  /**
   * Creates ID Manager without a support for <i>null IDs</i>.
   */
  public IdManager() {
  }

  /**
   * Creates ID Manager.
   * <p>
   * This constructor enables support for <i>null IDs</i> that are represented
   * by the given null identifier.<br/>
   * Such a null identifier must be provided by the implementors.
   * </p>
   * 
   * @param nullIdentifier
   *          The null identifier instance that will represent <tt>ID == 0</tt>.
   */
  public IdManager(I nullIdentifier) {
    identifierSet.put(nullIdentifier, new WeakReference<I>(nullIdentifier));
    objectToIdentifierMap.put(null, nullIdentifier);
  }

  /**
   * The map that stores the set of all available identifiers.<br/>
   * A particular identifier can be retrieved using the
   * {@link IdentifierPointer} instance.
   */
  private Map<I, WeakReference<I>> identifierSet = new WeakHashMap<I, WeakReference<I>>();

  /**
   * A map that translates VM objects into an identifier.
   */
  private Map<T, I> objectToIdentifierMap = new WeakHashMap<T, I>();

  /**
   * Zero value is for the nullIdentifier
   */
  private Long idGenerator = (long) 1;

  /**
   * Gets or creates an identifier for the given VM object.
   * 
   * @param object
   *          The VM object that has to be represented by an identifier.
   * @return The identifier to represent the VM object in the whole JPDA.
   */
  public synchronized I getIdentifierId(T object) {

    if (objectToIdentifierMap.containsKey(object)) {
      // identifier exists

      I identifier = objectToIdentifierMap.get(object);
      logger.debug("Identifier for object: '{}' found: '{}'", object, identifier);
      return identifier;

    } else {
      // identifier doesn't exist, lets create one
      
      if (object == null) {
        // if null object is supported we won't be here since containsKey will return true
        throw new IllegalStateException("NULL objects not supported by this ID Manager instance! " + this);
      }

      Long id = idGenerator++;
      I identifier = createIdentifier(id, object);
      objectToIdentifierMap.put(object, identifier);
      identifierSet.put(identifier, new WeakReference<I>(identifier));

      if (logger.isDebugEnabled()) {
        if (object instanceof ElementInfo) {
          logger.debug("Created ID: {}, (identifier: {}) object: {}, class: {}, classInfo: {}", id, identifier, object, object.getClass(),
                       ((ElementInfo) object).getClassInfo());
        } else {
          logger.debug("Created ID: {}, (identifier: {}) object: {}, class: {}", id, identifier, object, object.getClass());
        }
      }

      return identifier;
    }
  }

  /**
   * Gets an identifier for the given ID.<br/>
   * If the given ID represents an object that was discarded, exception is
   * thrown.
   * 
   * @param id
   *          The ID that an identifier is looked for.
   * @return The identifier.
   * @throws E
   *           If the object that is represented by the given ID was discarded.
   */
  private synchronized I get(long id) throws E {
    IdentifierPointer pointer = new IdentifierPointer(id);
    Reference<I> ref = identifierSet.get(pointer);
    if (ref == null) {
      // there is no such ID registered by this ID manager
      throw identifierNotFound(id);
    }
    I identifier = ref.get();
    if (identifier == null) {
      // this may happen only if <tt>identifier</tt> was just discarded between
      // the <tt>get</tt> call from the hashmap
      // and the get call of the reference
      if (identifierSet.get(pointer) == null) {
        // it really happened! The object collection really happened between the
        // two <tt>get</tt> calls above
        throw identifierNotFound(id);
      } else {
        // this should not ever happen... see the theory behind explanation in
        // this file
        throw new IllegalStateException("The idea behind this manager prooved to be wrong! This is very very bad!");
      }
    }
    return identifier;
  }

  /**
   * Reads an identifier from the buffer of bytes.
   * 
   * @param bytes
   *          The buffer of bytes to read the identifier from.
   * @return The identifier.
   * @throws E
   *           If the identifier was discarded or was not registered by this ID
   *           manager.
   */
  public I readIdentifier(ByteBuffer bytes) throws E {
    return get(bytes.getLong());
  }

  /**
   * Create Identifier for the given object to be represented by the given ID
   * for the JDWP communication.<br/>
   * This is how subtypes of this class define which Identifier subtypes are
   * represented by this ID manager.
   * 
   * @param id
   *          The ID to represent the given object.
   * @param object
   *          The object to be represented by the ID
   * @return The identifier encapsulation of the given ID for the given object.
   */
  public abstract I createIdentifier(Long id, T object);

  /**
   * This is how subtypes of this class define the exception to be thrown if an
   * identifier was not found which means it did not exist or it was discarded.
   * 
   * @param id
   *          The ID of the desired identifier.
   * @return An exception that will be thrown.
   */
  public abstract E identifierNotFound(long id);
}
