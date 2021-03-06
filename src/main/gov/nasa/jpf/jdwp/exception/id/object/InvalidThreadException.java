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
 * Passed thread is null, is not a valid thread or has exited.
 * </p>
 * 
 * @author stepan
 * 
 */
public class InvalidThreadException extends InvalidObjectException {

  /**
	 * 
	 */
  private static final long serialVersionUID = 203403989030955960L;

  /**
   * Constructs the {@link InvalidThreadException} exception.
   * 
   * @param objectId
   *          The invalid objectID to report.
   * @see InvalidObjectException
   * @see InvalidThreadException
   */
  public InvalidThreadException(ObjectId objectId) {
    super(ErrorType.INVALID_THREAD, objectId);
  }

  /**
   * Constructs the {@link InvalidThreadException} exception.
   * 
   * @param objectId
   *          The invalid objectID to report.
   * @param cause
   *          The exception cause.
   * @see InvalidObjectException
   * @see InvalidThreadException
   */
  public InvalidThreadException(ObjectId objectId, Throwable cause) {
    super(ErrorType.INVALID_THREAD, objectId, cause);
  }

  /**
   * Constructs the {@link InvalidThreadException} exception.
   * 
   * @param id
   *          The invalid ID to report.
   * @see InvalidObjectException
   * @see InvalidThreadException
   */
  public InvalidThreadException(long id) {
    super(ErrorType.INVALID_THREAD, id);
  }

}
