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
