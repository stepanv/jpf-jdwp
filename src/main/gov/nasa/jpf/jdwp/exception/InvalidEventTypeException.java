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

import gov.nasa.jpf.jdwp.event.EventBase.EventKind;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * The specified event type id is not recognized.
 * </p>
 * 
 * @author stepan
 * 
 */
public class InvalidEventTypeException extends IllegalArgumentException {

  /**
	 * 
	 */
  private static final long serialVersionUID = -4564896905569473797L;

  /**
   * Constructs {@link InvalidEventTypeException} with a message based on the
   * invalid event kind.
   * 
   * @param eventKind
   *          The invalid event kind.
   * @param e
   *          The cause.
   */
  public InvalidEventTypeException(EventKind eventKind) {
    this(" Invalid event type: " + eventKind, null);
  }

  /**
   * Constructs {@link InvalidEventTypeException} with a message based on the
   * invalid ID.
   * 
   * @param eventId
   *          The invalid ID.
   * @param e
   *          The cause.
   */
  public InvalidEventTypeException(Byte eventId, Throwable e) {
    this("Invalid id: '" + eventId + "' byte.", e);
  }

  /**
   * Constructs {@link InvalidEventTypeException} with a message based on the
   * provided message.
   * 
   * @param message
   *          The message to report.
   * @param e
   *          The cause.
   */
  public InvalidEventTypeException(String message, Throwable cause) {
    super(ErrorType.INVALID_EVENT_TYPE, message, cause);
  }

}
