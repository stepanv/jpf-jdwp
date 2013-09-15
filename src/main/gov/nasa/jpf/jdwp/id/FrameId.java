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

package gov.nasa.jpf.jdwp.id;

import gov.nasa.jpf.jdwp.exception.id.InvalidFrameIdException;
import gov.nasa.jpf.vm.StackFrame;

/**
 * This class implements the corresponding <code>frameID</code> common data type
 * from the JDWP Specification.
 * 
 * <p>
 * <h2>JDWP Specification</h2>
 * Uniquely identifies a frame in the target VM. The frameID must uniquely
 * identify the frame within the entire VM (not only within a given thread). The
 * frameID need only be valid during the time its thread is suspended.
 * </p>
 * 
 * @author stepan
 * 
 */
public class FrameId extends IdentifierBase<StackFrame> {

  /**
   * Frame ID constructor.
   * 
   * @param id
   *          The numerical ID of this identifier.
   * @param stackFrame
   *          The {@link StackFrame} this identifier is created for.
   */
  public FrameId(Long id, StackFrame stackFrame) {
    super(id, stackFrame);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nasa.jpf.jdwp.id.Identifier#nullObjectHandler(gov.nasa.jpf.jdwp.id.
   * Identifier)
   */
  @Override
  public StackFrame nullObjectHandler() throws InvalidFrameIdException {
    throw new InvalidFrameIdException(this);
  }

}
