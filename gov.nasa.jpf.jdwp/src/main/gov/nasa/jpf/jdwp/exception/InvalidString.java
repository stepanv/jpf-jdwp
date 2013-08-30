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
 * The string is invalid.
 * 
 * Thrown when incoming string doesn't meet constraints.
 * 
 * @author stepan
 * 
 */
public class InvalidString extends JdwpError {

  /**
	 * 
	 */
  private static final long serialVersionUID = 8157720539310443228L;

  /**
   * Creates Invalid String exception.
   * 
   * @param invalidString
   *          The string that is invalid.
   */
  public InvalidString(String invalidString) {
    this(invalidString, null);
  }

  /**
   * Creates Ivalid String exception.
   * 
   * @param invalidString
   *          The string that is invalid.
   * @param message
   *          Additional message explaining why the string is invalid.
   */
  public InvalidString(String invalidString, String message) {
    super(ErrorType.INVALID_STRING, "Invalid string: '" + invalidString + "'." + message != null ? " " + message : "");
  }

  /**
   * Creates Ivalid String exception.
   * 
   * @param invalidString
   *          The string that is invalid.
   * @param message
   *          Additional message explaining why the string is invalid.
   * @param cause
   *          The cause of this exception.
   */
  public InvalidString(String invalidString, String message, Throwable cause) {
    super(ErrorType.INVALID_STRING, "Invalid string: '" + invalidString + "'." + message != null ? " " + message : "", cause);
  }

}
