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

package gov.nasa.jpf.jdwp.exception;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Object type id or class tag.
 * </p>
 * 
 * @author stepan
 * 
 */
public class InvalidTagException extends IllegalArgumentException {

  /**
   * 
   */
  private static final long serialVersionUID = 1890933796698299280L;

  /**
   * Constructs the {@link InvalidTagException} exception.
   * 
   * @param message
   *          The message to report.
   * @param cause
   *          The cause exception.
   */
  public InvalidTagException(String message, Throwable cause) {
    super(ErrorType.INVALID_TAG, message, cause);
  }

  /**
   * Constructs the {@link InvalidTagException} exception.
   * 
   * @param tagId
   *          The invalid ID to report.
   * @param cause
   *          The cause exception.
   */
  public InvalidTagException(Byte tagId, Throwable cause) {
    this("Invalid id: '" + tagId + "' byte.", cause);
  }

}
