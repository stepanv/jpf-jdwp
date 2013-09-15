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

package gov.nasa.jpf.jdwp.exception.id;

import gov.nasa.jpf.jdwp.id.FieldId;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Invalid field.
 * </p>
 * 
 * @author stepan
 * 
 */
public class InvalidFieldIdException extends InvalidIdentifierException {

  /**
	 * 
	 */
  private static final long serialVersionUID = 2187774892944476187L;

  /**
   * Constructs the {@link InvalidFieldIdException} exception.
   * 
   * @param id
   *          The invalid ID to report.
   * @see InvalidIdentifierException
   * @see InvalidFieldIdException
   */
  public InvalidFieldIdException(long id) {
    super(ErrorType.INVALID_FIELDID, id);
  }

  /**
   * Constructs the {@link InvalidFieldIdException} exception.
   * 
   * @param fieldId
   *          The invalid fieldID to report.
   * @see InvalidIdentifierException
   * @see InvalidFieldIdException
   */
  public InvalidFieldIdException(FieldId fieldId) {
    super(ErrorType.INVALID_FIELDID, fieldId);
  }

}
