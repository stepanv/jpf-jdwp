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

package gov.nasa.jpf.jdwp.exception.special;

/**
 * The class {@link NoPropertyViolationException} is intended to be used only
 * and only as a <i>Property Violation</i> notification to the debugger.
 * 
 * <p>
 * The property violation notification feature can be enabled from Eclipse JPF
 * plug-in.
 * </p>
 * 
 * <p>
 * Warning! - The canonical name of this method is used by the
 * <code>JPFLaunchConfigurationDelegate</code> from the <i>Eclipse JPF</i>
 * plug-in and therefore changes to this class must be reflected there too!
 * </p>
 * 
 * @author stepan
 * 
 */
public final class NoPropertyViolationException extends Throwable {

  /**
   * 
   */
  private static final long serialVersionUID = -6475746056456154686L;
}
