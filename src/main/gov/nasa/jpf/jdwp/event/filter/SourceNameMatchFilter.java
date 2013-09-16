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

import gov.nasa.jpf.jdwp.VirtualMachine.CapabilitiesNew;
import gov.nasa.jpf.jdwp.event.SourceNameMatchFilterable;

/**
 * <p>
 * 
 * <h2>JDWP Specification</h2>
 * Restricts reported class prepare events to those for reference types which
 * have a source name which matches the given restricted regular expression. The
 * source names are determined by the reference type's SourceDebugExtension.
 * This modifier can only be used with class prepare events. <br/>
 * 
 * Requires the {@link CapabilitiesNew#CAN_USE_SOURCE_NAME_FILTERS} capability.
 * </p>
 * 
 * @since JDWP version 1.6.
 * @author stepan
 * 
 */
public class SourceNameMatchFilter extends Filter<SourceNameMatchFilterable> {
  private String sourceNamePattern;

  /**
   * Creates Source Name Match filter for the given source name pattern
   * parameter.
   * 
   * @param sourceNamePattern
   *          Required source name pattern. Matches are limited to exact matches
   *          of the given pattern and matches of patterns that begin or end
   *          with '*'; for example, "*.Foo" or "java.*".
   */
  public SourceNameMatchFilter(String sourceNamePattern) {
    super(ModKind.SOURCE_NAME_MATCH, SourceNameMatchFilterable.class);
    this.sourceNamePattern = sourceNamePattern;
  }

  @Override
  public boolean matches(SourceNameMatchFilterable event) {
    throw new RuntimeException("NOT IMPLEMENTED YET " + event + " ... " + sourceNamePattern);
  }

}
