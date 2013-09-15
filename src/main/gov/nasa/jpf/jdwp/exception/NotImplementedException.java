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
 * The functionality is not implemented in this virtual machine.
 * </p>
 * 
 * @author stepan
 * 
 */
public class NotImplementedException extends JdwpException {

  /**
	 * 
	 */
  private static final long serialVersionUID = -3022850035823199665L;

  /**
   * Creates the Not implemented exception.
   */
  public NotImplementedException() {
    super(ErrorType.NOT_IMPLEMENTED);
  }

  /**
   * Creates the Not implemented exception.
   * 
   * @param message
   *          The message to be reported.
   */
  public NotImplementedException(String message) {
    super(ErrorType.NOT_IMPLEMENTED, message);
  }

}
