package gov.nasa.jpf.jdwp.event.filter;

import gnu.classpath.jdwp.exception.InvalidThreadException;
import gnu.classpath.jdwp.id.ThreadId;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;

import java.util.Iterator;

/**
 * Step out of the current method.
 * 
 * @author stepan
 * 
 */
public class StepOutFilter extends StepFilter {

	public StepOutFilter(ThreadId thread, StepSize size, Iterator<StackFrame> stackFrameIterator) throws InvalidThreadException {
		super(thread, size, stackFrameIterator);
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
