package gov.nasa.jpf.jdwp.event.filter;

import gnu.classpath.jdwp.exception.InvalidThreadException;
import gnu.classpath.jdwp.id.ThreadId;
import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.EventRequest;
import gov.nasa.jpf.jdwp.exception.JdwpException;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.bytecode.EXECUTENATIVE;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;

import java.util.Iterator;

/**
 * Step into any method calls that occur before the end of the step.
 * 
 * @author stepan
 * 
 */
public class StepIntoFilter extends StepFilter {

	public StepIntoFilter(ThreadId thread, StepSize size, Iterator<StackFrame> stackFrameIterator) throws InvalidThreadException {
		super(thread, size, stackFrameIterator);
	}

	@Override
	protected boolean matches(int currentStackFrameSize, Instruction currentInstruction) {
		/* we just stepped in some method */
		if (currentStackFrameSize > stackSnapshot.size()) {

			/*
			 * we're accepting only method very beginnings and we don't
			 * step into native methods
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
			 * we're about to enter another method at the same line from
			 * where our method was invoked
			 */
			if (currentInstruction instanceof InvokeInstruction) {

				/*
				 * this might be tricky because instance of
				 * InvokeInstruction could also invoke just a native or
				 * a synthetic method where we won't be able to step in.
				 * 
				 * Let's do not care about this...
				 */
				return true;
			}

		}

		return false;
	}

}
