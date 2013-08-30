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

package gov.nasa.jpf.jdwp.event.filter;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Restricts reported events to those for classes whose name matches the given
 * restricted regular expression. For class prepare events, the prepared class
 * name is matched. For class unload events, the unloaded class name is matched.
 * For other events, the class name of the event's location is matched. This
 * modifier can be used with any event kind except thread start and thread end.
 * </p>
 * 
 * @author stepan
 * 
 */
public class ClassMatchFilter extends ClassFilter {

  /**
   * <p>
   * Creates Class Match Filter for the given restricted regular expression.<br/>
   * Be aware that we're not talking about standard regular expressions.
   * </p>
   * 
   * @see ClassFilter
   * @param classPattern
   *          Required class pattern. Matches are limited to exact matches of
   *          the given class pattern and matches of patterns that begin or end
   *          with '*'; for example, "*.Foo" or "java.*".
   */
  public ClassMatchFilter(String classPattern) {
    super(ModKind.CLASS_MATCH, classPattern);
  }

  @Override
  public boolean matches(String className) {
    return compare(className);
  }

}
