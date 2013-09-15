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
 * If the specified thread has not been suspended by an event.
 * </p>
 * 
 * @author stepan
 * 
 */
public class ThreadNotSuspendedException extends JdwpException {

  /**
	 * 
	 */
  private static final long serialVersionUID = -1500457710057267303L;

  /**
   * Creates the Thread not suspended exception.
   */
  public ThreadNotSuspendedException() {
    super(ErrorType.THREAD_NOT_SUSPENDED);
  }

  /**
   * Creates the Thread not suspended exception.
   * 
   * @param message
   *          The message to be reported.
   */
  public ThreadNotSuspendedException(String message) {
    super(ErrorType.THREAD_NOT_SUSPENDED, message);
  }

  /**
   * Creates the Thread not suspended exception.
   * 
   * @param cause
   *          The exception cause to be chained.
   */
  public ThreadNotSuspendedException(Throwable cause) {
    super(ErrorType.THREAD_NOT_SUSPENDED, cause);
  }

  /**
   * Creates the Thread not suspended exception.
   * 
   * @param message
   *          The message to be reported.
   * @param cause
   *          The exception cause to be chained.
   */
  public ThreadNotSuspendedException(String message, Throwable cause) {
    super(ErrorType.THREAD_NOT_SUSPENDED, message, cause);
  }

}
