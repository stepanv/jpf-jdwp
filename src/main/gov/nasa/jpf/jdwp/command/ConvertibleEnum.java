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

package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.IllegalArgumentException;

/**
 * This interface provides a simple infrastructure for enums so that some
 * particular ID of an enum instance (whose type is denoted by the generics) can
 * be simply converted to the enum instance itself.<br/>
 * Note that since enums cannot inherit a particular class all of them must
 * implement the convert logic by themselves.
 * 
 * @author stepan
 * 
 * @param <T>
 *          The type that is used to identify the enum ID.
 * @param <E>
 *          The type of the enum instance (ie. the Enum class that implements
 *          this interface).
 * 
 * @see ReverseEnumMap
 */
public interface ConvertibleEnum<T, E extends Enum<E> & ConvertibleEnum<T, E>> extends IdentifiableEnum<T> {

  /**
   * Convert the given ID to the enum instance.
   * 
   * @param value
   *          The ID to convert.
   * @return The enum instance.
   * @throws IllegalArgumentException
   *           If there is no mapping for the given ID.
   */
  E convert(T value) throws IllegalArgumentException;
}
