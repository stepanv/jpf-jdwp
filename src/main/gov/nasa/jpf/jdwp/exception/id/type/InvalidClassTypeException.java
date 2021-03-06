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
 * Invalid class.
 * </p>
 * 
 * @author stepan
 * 
 */
public class InvalidClassTypeException extends InvalidReferenceTypeException {

  /**
   * 
   */
  private static final long serialVersionUID = 8985570072835568615L;

  /**
   * Constructs the {@link InvalidReferenceTypeException} exception.
   * 
   * @param id
   *          The invalid ID to report.
   */
  public InvalidClassTypeException(long id) {
    super(ErrorType.INVALID_CLASS, id);
  }

  /**
   * Constructs the {@link InvalidReferenceTypeException} exception.
   * 
   * @param referenceTypeId
   *          The invalid reference type ID to report.
   */
  public InvalidClassTypeException(ReferenceTypeId referenceTypeId) {
    super(ErrorType.INVALID_CLASS, referenceTypeId);
  }

}
