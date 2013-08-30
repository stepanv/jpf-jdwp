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

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;

import java.util.HashMap;
import java.util.Map;

public class ReverseEnumMap<T, V extends Enum<V> & IdentifiableEnum<T>> {
  private Map<T, V> map = new HashMap<T, V>();

  public ReverseEnumMap(Class<V> enumIdentifierClassType) {
    for (V v : enumIdentifierClassType.getEnumConstants()) {
      if (v.identifier() != null) {
        map.put(v.identifier(), v);
      }
    }
  }

  public V get(T num) throws JdwpError {
    V value = map.get(num);
    if (value != null) {
      return value;
    }
    throw new JdwpError(ErrorType.NOT_IMPLEMENTED, "Mapping doesn't exist for: " + num);
  }

}
