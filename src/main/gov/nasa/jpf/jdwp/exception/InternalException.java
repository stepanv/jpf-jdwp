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
 * This exception stands for any errors that may have happened during the
 * execution of JDWP commands.<br/>
 * Errors of this type are rather bugs than anything else.
 * </p>
 * 
 * <p>
 * <h2>JDWP Specification</h2>
 * An unexpected internal error has occurred.
 * </p>
 * 
 * @author stepan
 * 
 */
public class InternalException extends JdwpException {

  /**
	 * 
	 */
  private static final long serialVersionUID = -8092968568063765998L;

  /**
   * Constructs the {@link InternalException} exception.
   * 
   * @see InternalException
   */
  public InternalException() {
    super(ErrorType.INTERNAL);
  }

  /**
   * Constructs the {@link InternalException} exception.
   * 
   * @param message
   *          The message to report.
   * @see InternalException
   */
  public InternalException(String message) {
    super(ErrorType.INTERNAL, message);
  }

  /**
   * Constructs the {@link InternalException} exception.
   * 
   * @param cause
   *          The cause of this exception.
   * @see InternalException
   */
  public InternalException(Throwable cause) {
    super(ErrorType.INTERNAL, cause);
  }

  /**
   * Constructs the {@link InternalException} exception.
   * 
   * @param message
   *          The message to report.
   * @param cause
   *          The cause of this exception.
   * @see InternalException
   */
  public InternalException(String message, Throwable cause) {
    super(ErrorType.INTERNAL, message, cause);
  }

}
