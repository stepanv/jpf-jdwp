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

package gov.nasa.jpf.jdwp.exception.id.object;

import gov.nasa.jpf.jdwp.id.object.ObjectId;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Clazz is not the ID of a class.
 * </p>
 * 
 * Note that {@link ErrorType#INVALID_CLASS} is used for both
 * <ul>
 * <li><i>Clazz is not the ID of a class.</i></li>
 * <li><i>RefType is not the ID of a reference type.</i></li>
 * </ul>
 * according to the JDWP Specification.<br/>
 * That might be confusing; however, this JDWP implementation has all the
 * reference types managed by a dedicated ID manager hence it's impossible to
 * end with the latter error state.
 * 
 * @author stepan
 * 
 */
public class InvalidClassObjectException extends InvalidObjectException {

  /**
	 * 
	 */
  private static final long serialVersionUID = -4264642841819420585L;

  /**
   * Constructs the {@link InvalidClassObjectException} exception.
   * 
   * @param objectId
   *          The invalid objectID to report.
   * @see InvalidObjectException
   * @see InvalidClassObjectException
   */
  public InvalidClassObjectException(ObjectId objectId) {
    super(ErrorType.INVALID_CLASS, objectId);
  }

  /**
   * Constructs the {@link InvalidClassObjectException} exception.
   * 
   * @param id
   *          The invalid ID to report.
   * @see InvalidObjectException
   * @see InvalidClassObjectException
   */
  public InvalidClassObjectException(long id) {
    super(ErrorType.INVALID_CLASS, id);
  }

}
