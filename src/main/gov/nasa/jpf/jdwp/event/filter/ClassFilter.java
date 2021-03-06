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

import gov.nasa.jpf.jdwp.command.EventRequestCommand;
import gov.nasa.jpf.jdwp.event.ClassFilterable;
import gov.nasa.jpf.jdwp.exception.JdwpException.ErrorType;

/**
 * Class filters allow only certain classes, matching the provided pattern, to
 * be reported.
 * <p>
 * Even though the JDWP specification is talking about regular expressions the
 * pattern is actually very limited. <br/>
 * We and also both OpenJDK and Harmony accept any pattern, since no pattern is
 * invalid (this actually requires little bit more brain activity).<br/>
 * As a result (in a combination with {@link ClassExcludeFilter} filter) the
 * command {@link EventRequestCommand#SET} never returns
 * {@link ErrorType#INVALID_STRING}.
 * </p>
 * Apparently, this might cause some confusion as JDWP Specification clearly
 * states that {@link ErrorType#INVALID_STRING} can be returned. </p>
 * <p>
 * 
 * @author stepan
 * 
 */
public abstract class ClassFilter extends Filter<ClassFilterable> {

  /**
   * <p>
   * Creates Class Match Filter for the given restricted regular expression.<br/>
   * Be aware that we're not talking about standard regular expressions.
   * </p>
   * 
   * @see ClassMatchFilter
   * @param classPattern
   *          Required class pattern. Matches are limited to exact matches of
   *          the given class pattern and matches of patterns that begin or end
   *          with '*'; for example, "*.Foo" or "java.*".
   */
  public ClassFilter(ModKind modKind, String classPattern) {
    super(modKind, ClassFilterable.class);

    this.classPattern = classPattern;
  }

  private String classPattern;

  private static final char ASTERISK = '*';

  @Override
  public boolean matches(ClassFilterable event) {
    return event.matches(this);
  }

  /**
   * Whether the given parameter, which is actually a class signature is
   * accepted by this filter according to the provided pattern.
   * 
   * @param className
   *          The class signature.
   * @return True or false as a result of filtering.
   */
  public abstract boolean matches(String className);

  protected boolean compare(String className) {
    if (classPattern.charAt(0) == ASTERISK) {
      return className.endsWith(classPattern.substring(1));
    }

    int end = classPattern.length() - 1;
    if (classPattern.charAt(end) == ASTERISK) {
      return className.startsWith(classPattern.substring(0, end));
    }

    return className.equals(classPattern);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(" [class pattern: ").append(classPattern).append("]");
    return super.toString() + sb;
  }

}
