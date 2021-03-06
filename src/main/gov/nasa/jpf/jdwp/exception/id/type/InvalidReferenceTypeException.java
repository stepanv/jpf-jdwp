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

import gov.nasa.jpf.jdwp.exception.id.InvalidIdentifierException;
import gov.nasa.jpf.jdwp.exception.id.object.InvalidObjectException;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * refType is not a known ID.
 * </p>
 * 
 * @see InvalidObjectException
 * @author stepan
 * 
 */
public class InvalidReferenceTypeException extends InvalidIdentifierException {

  /**
	 * 
	 */
  private static final long serialVersionUID = 5445794498004684701L;

  /**
   * Constructs the {@link InvalidReferenceTypeException} exception.
   * 
   * @param referenceTypeId
   *          The invalid reference type ID to report.
   */
  public InvalidReferenceTypeException(ReferenceTypeId referenceTypeId) {
    super(ErrorType.INVALID_OBJECT, referenceTypeId);
  }

  /**
   * Constructs the {@link InvalidReferenceTypeException} exception.
   * 
   * @param id
   *          The invalid ID to report.
   */
  public InvalidReferenceTypeException(long id) {
    super(ErrorType.INVALID_OBJECT, id);
  }

  /**
   * Constructs the {@link InvalidReferenceTypeException} exception with a
   * support for subclassing.
   * 
   * @param errorType
   *          The Error Type.
   * @param referenceTypeId
   *          The invalid reference type ID to report.
   */
  protected InvalidReferenceTypeException(ErrorType errorType, ReferenceTypeId referenceTypeId) {
    super(errorType, referenceTypeId);
  }

  /**
   * Constructs the {@link InvalidReferenceTypeException} exception with a
   * support for subclassing.
   * 
   * @param errorType
   *          The Error Type.
   * @param id
   *          The invalid reference type ID to report.
   */
  protected InvalidReferenceTypeException(ErrorType errorType, long id) {
    super(errorType, id);
  }

}
