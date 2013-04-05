package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.exception.InvalidThreadException;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.vm.Instruction;

/**
 * Step over any method calls that occur before the end of the step.
 * 
 * @author stepan
 *
 */
public class StepOverFilter extends StepFilter {

	public StepOverFilter(ThreadId thread, StepSize size) throws InvalidThreadException {
		super(thread, size);
	}

	@Override
	protected boolean matches(int currentStackFrameSize, Instruction currentInstruction) {
		/* we're at the same stack depth as when step was requested */
		if (currentStackFrameSize == stackSnapshot.size()) {

			/* we're already on a different line */
			if (currentLineDiffers(currentInstruction)) {
				return true;
			}
		}

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
