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
import java.util.HashMap;
import java.util.Objects;

/**
 * The base implementation of a universal {@link Identifier} container.
 * 
 * @author stepan
 * 
 * @see JdwpIdManager
 * @see Identifier
 * @param <T>
 *          The type this identifier works as a container for.
 */
public abstract class IdentifierBase<T> implements Identifier<T> {

  private Long id;
  private WeakReference<T> objectReference;

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

  /*
   * (non-Javadoc)
   * @see gov.nasa.jpf.jdwp.id.Identifier#write(java.io.DataOutputStream)
   */
  @Override
  final public void write(DataOutputStream os) throws IOException {
    os.writeLong(id);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    try {
      return super.toString() + ", reference: " + get() + ", id: " + id;
    } catch (InvalidIdentifierException e) {
      return "invalid reference, id: " + id;
    }
  }

  /**
   * Overriden {@link Object#hashCode()} so that this object may be interchanged
   * with {@link IdentifierPointer} in {@link HashMap} and similar maps.
   */
  @Override
  public int hashCode() {
    return id.hashCode();
  }

  /**
   * Overriden {@link Object#equals(Object)} so that this object may be
   * interchanged with {@link IdentifierPointer} in {@link HashMap} and similar
   * maps.
   */
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
