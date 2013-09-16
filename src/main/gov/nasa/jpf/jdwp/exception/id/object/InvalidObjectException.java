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

import gov.nasa.jpf.jdwp.exception.id.InvalidIdentifierException;
import gov.nasa.jpf.jdwp.exception.id.reference.InvalidReferenceTypeException;
import gov.nasa.jpf.jdwp.id.object.ObjectId;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * If this reference type has been unloaded and garbage collected.<br/>
 * As well as object is not a known ID.
 * </p>
 * 
 * Note that {@link ErrorType#INVALID_OBJECT} is used for
 * <ol>
 * <li><i>If this reference type has been unloaded and garbage collected. </i></li>
 * <li><i>object is not a known ID. </i></li>
 * <li><i>RefType is not a known ID.</i></li>
 * </ol>
 * errors according to the JDWP Specification.<br/>
 * That might be confusing...
 * 
 * @see InvalidReferenceTypeException
 * @author stepan
 * 
 */
public class InvalidObjectException extends InvalidIdentifierException {

  /**
	 * 
	 */
  private static final long serialVersionUID = -3401121682523839373L;

  /**
   * Constructs the {@link InvalidObjectException} exception.
   * 
   * @param objectId
   *          The invalid objectID to report.
   * @see InvalidObjectException
   */
  public InvalidObjectException(ObjectId objectId) {
    super(ErrorType.INVALID_OBJECT, objectId);
  }

  /**
   * Constructs the {@link InvalidObjectException} exception.
   * 
   * @param id
   *          The invalid ID to report.
   * @see InvalidObjectException
   */
  public InvalidObjectException(long id) {
    super(ErrorType.INVALID_OBJECT, id);
  }

  /**
   * Constructs the {@link InvalidObjectException} exception with a support for
   * subclassing.
   * 
   * @param errorType
   *          The Error type.
   * @param objectId
   *          The invalid objectID to report.
   * @see InvalidObjectException
   */
  protected InvalidObjectException(ErrorType errorType, ObjectId objectId) {
    super(errorType, objectId);
  }

  /**
   * Constructs the {@link InvalidObjectException} exception with a support for
   * subclassing.
   * 
   * @param errorType
   *          The Error type.
   * @param objectId
   *          The invalid objectID to report.
   * @param cause
   *          The cause exception.
   * @see InvalidObjectException
   */
  protected InvalidObjectException(ErrorType errorType, ObjectId objectId, Throwable cause) {
    super(errorType, objectId);
  }

  /**
   * Constructs the {@link InvalidObjectException} exception with a support for
   * subclassing.
   * 
   * @param errorType
   *          The Error type.
   * @param id
   *          The invalid ID to report.
   * @see InvalidObjectException
   */
  protected InvalidObjectException(ErrorType errorType, long id) {
    super(errorType, id);
  }
}
