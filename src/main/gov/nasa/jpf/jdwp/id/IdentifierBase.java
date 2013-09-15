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
public abstract class IdentifierBase<T> implements Identifier<T> {

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
  public IdentifierBase(Long id, T object) {
    this.objectReference = new WeakReference<T>(object);
    this.id = id;
  }

  @Override
  public boolean isNull() {
    return objectReference.get() == null;
  }

  @Override
  public T get() throws InvalidIdentifierException {
    T object = objectReference.get();

    if (object == null) {
      return nullObjectHandler();
    }
    return object;
  }

  @Override
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
    if (obj instanceof IdentifierBase<?>) {
      return Objects.equals(id, ((IdentifierBase<?>) obj).id)
          && Objects.equals(objectReference.get(), ((IdentifierBase<?>) obj).objectReference.get());
    }
    return false;
  }

}
