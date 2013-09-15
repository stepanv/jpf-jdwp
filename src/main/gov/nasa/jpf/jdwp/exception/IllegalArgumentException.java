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
 * Illegal argument.
 * </p>
 * 
 * <p>
 * This class also aggregates other exception with <i>invalid argument
 * nature</i>. Such a hierarchy is not mentioned by the specification; however,
 * a hierarchy in exceptions is a good thing in general and is required by the
 * this implementation.
 * </p>
 * 
 * @author stepan
 * 
 */
public class IllegalArgumentException extends JdwpException {

  /**
	 * 
	 */
  private static final long serialVersionUID = 5106332507228617820L;

  /**
   * Constructs the {@link IllegalArgumentException} exception.
   * 
   * @param message
   *          The message to be reported.
   */
  public IllegalArgumentException(String message) {
    super(ErrorType.ILLEGAL_ARGUMENT, message);
  }

  /**
   * For the support of other exceptions that represent a specific illegal
   * argument.
   * 
   * @param eventType
   *          The event type.
   * @param message
   *          The message to be reported.
   * @param cause
   *          The cause.
   */
  protected IllegalArgumentException(ErrorType eventType, String message, Throwable cause) {
    super(eventType, message, cause);
  }

}
