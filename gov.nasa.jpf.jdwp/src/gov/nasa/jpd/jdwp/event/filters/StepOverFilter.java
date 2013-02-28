package gov.nasa.jpd.jdwp.event.filters;

import gnu.classpath.jdwp.exception.InvalidThreadException;
import gnu.classpath.jdwp.id.ThreadId;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;

import java.util.Iterator;

/**
 * Step over any method calls that occur before the end of the step.
 * 
 * @author stepan
 *
 */
public class StepOverFilter extends StepFilter {

	public StepOverFilter(ThreadId thread, StepSize size, Iterator<StackFrame> stackFrameIterator) throws InvalidThreadException {
		super(thread, size, stackFrameIterator);
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
