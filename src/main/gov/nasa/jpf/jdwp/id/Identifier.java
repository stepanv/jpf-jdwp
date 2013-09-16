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
 *          The type this identifier act as a container for.
 */
public interface Identifier<T> {

  public static final long NULL_IDENTIFIER_ID = 0L;
  public static int SIZE = 8;

  /**
   * Whether this identifier represents a <tt>null</tt> object.
   * 
   * @return True or False.
   */
  public boolean isNull();

  /**
   * Forward the control of what happens if this identifier represents
   * represents a <tt>null</tt> object.
   * 
   * @return An object the null identifier represents.
   * @throws InvalidIdentifierException
   *           Or may throw an exception.
   */
  public T nullObjectHandler() throws InvalidIdentifierException;

  /**
   * Gets the object this identifier represents.<br/>
   * If it is null, the result of {@link Identifier#nullObjectHandler()} is
   * returned (or thrown).
   * 
   * @return The object this identifier represents.
   * @throws InvalidIdentifierException
   *           If this identifier is invalid.
   */
  public T get() throws InvalidIdentifierException;

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
  public void write(DataOutputStream os) throws IOException;

}
