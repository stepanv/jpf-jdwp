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

import gov.nasa.jpf.jdwp.event.filter.CountFilter;

/**
 * The count is invalid.
 * 
 * This exception applies only for {@link CountFilter}.
 * 
 * @author stepan
 * 
 */
public class InvalidCount extends JdwpError {

  /**
	 * 
	 */
  private static final long serialVersionUID = 6057046391915954062L;

  /**
   * Creates Invalid Count Exception.
   * 
   * @param count
   *          The invalid count.
   */
  public InvalidCount(int count) {
    super(ErrorType.INVALID_COUNT, "Invalid count: '" + count + "' provided.");
  }

}
