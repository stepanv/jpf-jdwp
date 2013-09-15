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

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link ReverseEnumMap} class provides an implementation for requirements
 * of {@link ConvertibleEnum} interface.
 * <p>
 * An enum that implements {@link ConvertibleEnum} enum needs to
 * <ol>
 * <li>add a private static field of a type {@link ReverseEnumMap} such as
 * 
 * <pre>
 * private static ReverseEnumMap&lt;{T}, {V}&gt; map = new ReverseEnumMap&lt;&gt;({V}.class);
 * </pre>
 * 
 * </li>
 * <li>and also the method {@link ConvertibleEnum#convert(Object)}:
 * 
 * <pre>
 * &#064;Override
 * public {V} convert({T} val) throws JdwpException {
 *   return map.get(val);
 * }
 * </pre>
 * 
 * </li>
 * </ol>
 * where <code>{V}</code> is the enum and must be substituted with it's real
 * name and the <tt>{T}</tt> is the type of the ID that works with
 * {@link IdentifiableEnum} and must be substituted as well.
 * </p>
 * 
 * @author stepan
 * 
 * @param <T>
 *          The type that goes to {@link IdentifiableEnum} which is how the
 *          instances of this enum are identified
 * @param <V>
 *          The enum that implements the {@link ConvertibleEnum}
 */
public class ReverseEnumMap<T, V extends Enum<V> & ConvertibleEnum<T, V>> {
  private Map<T, V> map = new HashMap<T, V>();

  /**
   * The constructor of the Reverse Enum helper Map.
   * 
   * @param enumIdentifierClassType
   *          The clazz of the enum to make convertible.
   */
  public ReverseEnumMap(Class<V> enumIdentifierClassType) {
    for (V v : enumIdentifierClassType.getEnumConstants()) {
      if (v.identifier() != null) {
        map.put(v.identifier(), v);
      }
    }
  }

  /**
   * The translation from the ID of the enum to its own instance.
   * 
   * @param num
   *          The ID of the enum instance.
   * @return The enum itself
   * @throws IllegalArgumentException
   *           If such ID doesn't exist id the given enum.
   */
  public V get(T num) throws IllegalArgumentException {
    V value = map.get(num);
    if (value != null) {
      return value;
    }
    throw new IllegalArgumentException("Mapping doesn't exist for: " + num);
  }

  /**
   * Whether the given ID has a mapping to an enum instance.
   * 
   * @param num
   *          The ID
   * @return True or false.
   */
  public boolean isValid(T num) {
    return map.containsKey(num);
  }

}
