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

import gov.nasa.jpf.jdwp.id.object.ClassObjectId;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Clazz is not the ID of a class.
 * </p>
 * 
 * Note that {@link ErrorType#INVALID_CLASS} is ambiguous since it's used even
 * for reference type as the JDWP Specification states.
 * 
 * @see InvalidReferenceType
 * 
 * @author stepan
 * 
 */
public class InvalidClassObjectId extends InvalidObject {

  /**
	 * 
	 */
  private static final long serialVersionUID = -4264642841819420585L;

  public InvalidClassObjectId(ErrorType errorType, ClassObjectId classObjectId) {
    super(ErrorType.INVALID_CLASS, classObjectId);
  }

}
