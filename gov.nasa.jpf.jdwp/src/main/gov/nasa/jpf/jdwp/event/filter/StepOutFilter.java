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

import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.exception.InvalidThreadException;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.vm.Instruction;

/**
 * Step out of the current method.
 * 
 * @author stepan
 * 
 */
public class StepOutFilter extends StepFilter {

  public StepOutFilter(ThreadId thread, StepSize size) throws InvalidThreadException, InvalidObject {
    super(thread, size);
  }

  @Override
  protected boolean matches(int currentStackFrameSize, Instruction currentInstruction) {
    /* we just stepped out of some method */
    if (currentStackFrameSize < stackSnapshot.size()) {

      /* we're already on a different line */
      if (lineDiffers(stackSnapshot.size() - currentStackFrameSize, currentInstruction)) {
        return true;
      }

      /* we're about to enter another method at the same line */
      if (currentInstruction instanceof InvokeInstruction) {
        return true;
      }

    }
    return false;
  }

}
