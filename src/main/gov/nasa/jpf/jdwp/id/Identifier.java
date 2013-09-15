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

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * Universal identifier container for any object that needs to be referenced by
 * the debugger.<br/>
 * It is important to not prevent GC from collecting the referenced object if
 * there is no other reference.<br/>
 * It is also important to not store the reference anywhere else in the JDWP
 * back-end itself to not prevent the GC collection. The referenced object is
 * likely to be stored in the {@link JdwpIdManager} instance and there the
 * references must be intentionally handled with care.<br/>
 * It is also convenient to immediately reflect the referenced object collection
 * which is done by using {@link WeakReference} references.
 * 
 * @author stepan
 * 
 * @see JdwpIdManager
 * 
 * @param <T>
 */
public abstract class Identifier<T> {

  public static final long NULL_IDENTIFIER_ID = 0L;

  public static int SIZE = 8;
  private Long id;
  private WeakReference<T> objectReference;

  /**
   * This is here to keep the reference in case we don't want a garbage
   * collection
   * 
   * TODO [for PJA] do I do it correctly? Maybe this is completely wrong and I
   * need to tell JPF directly to not collect it
   */
  @SuppressWarnings("unused")
  private T object;

  /**
   * The constructor of an identifier.
   * 
   * @param id
   *          The ID.
   * @param object
   *          The object this identifier represents.
   */
  public Identifier(Long id, T object) {
    this.objectReference = new WeakReference<T>(object);
    this.id = id;
  }

  /**
   * Whether this identifier represents a <tt>null</tt> object.
   * 
   * @return True or False.
   */
  public boolean isNull() {
    return objectReference.get() == null;
  }

  /**
   * Forward the control of what happens if this identifier represents
   * represents a <tt>null</tt> object.
   * 
   * @return An object the null identifier represents.
   * @throws InvalidIdentifierException
   *           Or may throw an exception.
   */
  public abstract T nullObjectHandler() throws InvalidIdentifierException;

  /**
   * Gets the object this identifier represents.<br/>
   * If it is null, the result of {@link Identifier#nullObjectHandler()} is
   * returned (or thrown).
   * 
   * @return The object this identifier represents.
   * @throws InvalidIdentifierException
   *           If this identifier is invalid.
   */
  public T get() throws InvalidIdentifierException {
    T object = objectReference.get();

    if (object == null) {
      return nullObjectHandler();
    }
    return object;
  }

  /**
   * Writes identifier as is into the given stream. <br/>
   * If subclasses want to write by default with additional information they
   * must introduce new method with different signature.
   * 
   * @param os
   *          The stream where to write the identifier.
   * @throws IOException
   *           If an I/O Error occurs.
   */
  final public void write(DataOutputStream os) throws IOException {
    os.writeLong(id);
  }

  @Override
  public String toString() {
    try {
      return super.toString() + ", reference: " + get() + ", id: " + id;
    } catch (InvalidIdentifierException e) {
      return "invalid reference, id: " + id;
    }
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Long) {
      return id.equals(obj);
    }
    if (obj instanceof Identifier<?>) {
      return Objects.equals(id, ((Identifier<?>) obj).id)
          && Objects.equals(objectReference.get(), ((Identifier<?>) obj).objectReference.get());
    }
    return false;
  }

}
