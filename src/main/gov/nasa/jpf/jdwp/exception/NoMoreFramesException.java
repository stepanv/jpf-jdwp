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

import gov.nasa.jpf.vm.ThreadInfo;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * There are no more Java or JNI frames on the call stack.
 * </p>
 * 
 * @author stepan
 * 
 */
public class NoMoreFramesException extends JdwpException {

  /**
   * 
   */
  private static final long serialVersionUID = -7119340451844958236L;

  /**
   * The constructor of {@link NoMoreFramesException} exception.
   * 
   * @param thread
   *          The thread that has no more frames.
   */
  public NoMoreFramesException(ThreadInfo thread) {
    super(ErrorType.NO_MORE_FRAMES, " thread: " + thread);
  }

}
