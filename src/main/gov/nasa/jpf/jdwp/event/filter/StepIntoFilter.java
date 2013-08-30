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
import gov.nasa.jpf.jvm.bytecode.EXECUTENATIVE;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.vm.Instruction;

/**
 * Step into any method calls that occur before the end of the step.
 * 
 * @author stepan
 * 
 */
public class StepIntoFilter extends StepFilter {

  public StepIntoFilter(ThreadId thread, StepSize size) throws InvalidThreadException, InvalidObject {
    super(thread, size);
  }

  @Override
  protected boolean matches(int currentStackFrameSize, Instruction currentInstruction) {
    /* we just stepped in some method */
    if (currentStackFrameSize > stackSnapshot.size()) {

      /*
       * we're accepting only method very beginnings and we don't step into
       * native methods
       */
      if (currentInstruction.getInstructionIndex() == 0 && !(currentInstruction instanceof EXECUTENATIVE)) {
        return true;
      }

    }

    /* we're in the same method */
    if (currentStackFrameSize == stackSnapshot.size()) {

      /* we're already on different line */
      if (currentLineDiffers(currentInstruction)) {
        return true;
      }

    }

    /* we're in the caller's method */
    if (currentStackFrameSize < stackSnapshot.size()) {

      /* we're already on a different line */
      if (lineDiffers(stackSnapshot.size() - currentStackFrameSize, currentInstruction)) {
        return true;
      }

      /*
       * we're about to enter another method at the same line from where our
       * method was invoked
       */
      if (currentInstruction instanceof InvokeInstruction) {

        /*
         * this might be tricky because instance of InvokeInstruction could also
         * invoke just a native or a synthetic method where we won't be able to
         * step in.
         * 
         * Let's do not care about this...
         * 
         * That means the program will stop at the caller's method line so that
         * if user want's to step into some other method which is about to be
         * called user has a chance. If the method is native, this will be
         * impossible and the user will stay at the line (will not step
         * actually).
         * 
         * This behavior is imho acceptable.
         */
        return true;
      }

    }

    return false;
  }

}
