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
 * The string is invalid.
 * </p>
 * 
 * @author stepan
 * 
 */
public class InvalidStringException extends InvalidObjectException {

  /**
   * 
   */
  private static final long serialVersionUID = 4421808556960095471L;

  /**
   * Constructs the {@link InvalidStringException} exception.
   * 
   * @param objectId
   *          The invalid objectID to report.
   * @see InvalidObjectException
   */
  public InvalidStringException(ObjectId objectId) {
    super(ErrorType.INVALID_STRING, objectId);
  }

  /**
   * Constructs the {@link InvalidStringException} exception.
   * 
   * @param id
   *          The invalid ID to report.
   * @see InvalidObjectException
   */
  public InvalidStringException(long id) {
    super(ErrorType.INVALID_STRING, id);
  }

}
