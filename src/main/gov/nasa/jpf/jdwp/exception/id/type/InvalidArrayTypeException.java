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

package gov.nasa.jpf.jdwp.exception.id.type;

import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * The array is invalid.
 * </p>
 * 
 * @author stepan
 * 
 */
public class InvalidArrayTypeException extends InvalidReferenceTypeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1473659524834598695L;

  /**
   * Constructs the {@link InvalidReferenceTypeException} exception.
   * 
   * @param referenceTypeId
   *          The invalid reference type ID to report.
   */
  public InvalidArrayTypeException(ReferenceTypeId referenceTypeId) {
    super(ErrorType.INVALID_ARRAY, referenceTypeId);
  }

  /**
   * Constructs the {@link InvalidReferenceTypeException} exception.
   * 
   * @param id
   *          The invalid ID to report.
   */
  public InvalidArrayTypeException(long id) {
    super(ErrorType.INVALID_ARRAY, id);
  }

}
