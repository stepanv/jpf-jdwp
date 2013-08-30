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

public class AbsentInformationException extends JdwpError {

  /**
	 * 
	 */
  private static final long serialVersionUID = 4479627949608944900L;

  public AbsentInformationException() {
    super(ErrorType.ABSENT_INFORMATION);
  }

  public AbsentInformationException(String message) {
    super(ErrorType.ABSENT_INFORMATION, message);
  }

  public AbsentInformationException(Throwable cause) {
    super(ErrorType.ABSENT_INFORMATION, cause);
  }

  public AbsentInformationException(String message, Throwable cause) {
    super(ErrorType.ABSENT_INFORMATION, message, cause);
  }

}
