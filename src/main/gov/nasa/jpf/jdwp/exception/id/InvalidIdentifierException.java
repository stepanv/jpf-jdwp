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

package gov.nasa.jpf.jdwp.exception.id;

import gov.nasa.jpf.jdwp.exception.JdwpException;
import gov.nasa.jpf.jdwp.id.Identifier;

/**
 * The base exception class for universal representation of all error states
 * related to usage of incorrect identifier.
 * 
 * @author stepan
 * 
 */
public abstract class InvalidIdentifierException extends JdwpException {

  /**
	 * 
	 */
  private static final long serialVersionUID = -3101810848944813714L;
  private Identifier<?> identifier;
  private Long id;

  /**
   * Constructs the {@link InvalidIdentifierException} exception for subclasses.
   * 
   * @param errorType
   *          The Error Type.
   * @param identifier
   *          The invalid identifier to report.
   */
  protected InvalidIdentifierException(ErrorType errorType, Identifier<?> identifier) {
    super(errorType);
    this.identifier = identifier;
  }

  /**
   * Constructs the {@link InvalidIdentifierException} exception for subclasses.
   * 
   * @param errorType
   *          The Error Type.
   * @param id
   *          The invalid ID to report.
   */
  protected InvalidIdentifierException(ErrorType errorType, long id) {
    super(errorType);
    this.id = id;
  }

  @Override
  public String toString() {
    StringBuffer text = new StringBuffer(" ");
    if (id != null) {
      text.append("[ID: ").append(id).append("]");
    }
    if (identifier != null) {
      text.append("[Identifier: ").append(identifier).append("]");
    }
    return super.toString() + text;
  }
}
