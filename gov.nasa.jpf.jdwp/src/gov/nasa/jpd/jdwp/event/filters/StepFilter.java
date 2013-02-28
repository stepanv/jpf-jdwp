package gov.nasa.jpd.jdwp.event.filters;

import gnu.classpath.jdwp.VMIdManager;
import gnu.classpath.jdwp.exception.InvalidThreadException;
import gnu.classpath.jdwp.id.ThreadId;
import gov.nasa.jpd.jdwp.event.SingleStepEvent;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.EXECUTENATIVE;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * 
 * 
 * Restricts reported step events to those which satisfy depth and size
 * constraints. This modifier can be used with step event kinds only.
 * 
 * @author stepan
 * 
 */
public class StepFilter {

	private static Logger log = Logger.getLogger(StepFilter.class.getName());

	public static enum StepSize {
		/** Step by the minimum possible amount (often a bytecode instruction). */
		MIN(0),

		/**
		 * Step to the next source line unless there is no line number
		 * information in which case a MIN step is done instead.
		 */
		LINE(1);

		private int value;

		private StepSize(int value) {
			this.value = value;
		}

	}

	public static enum StepDepth {
		/** Step into any method calls that occur before the end of the step. */
		INTO,

		/** Step over any method calls that occur before the end of the step. */
		OVER,

		/** Step out of the current method. */
		OUT
	}

	/**
	 * Thread in which to step
	 */
	private ThreadId thread;

	/**
	 * Size of each step.
	 * 
	 * @see {@link StepSize}
	 */
	private StepSize size;

	/**
	 * Relative call stack limit.
	 * 
	 * @see {@link StepDepth}
	 */
	private StepDepth depth;

	/**
	 * A stack frame snapshot to refer when detecting a current program state.
	 * 
	 * @author stepan
	 * 
	 */
	private class StackFrameSnapshot {
		private Instruction pc;

		private StackFrameSnapshot(StackFrame frame) {
			pc = frame.getPC();

			log.finest("Adding frame snapshot of " + frame + ", instruction: " + pc + " (location: " + pc.getFileLocation() + ")");

			stackSnapshot.add(this);
		}
	}

	private List<StackFrameSnapshot> stackSnapshot = new ArrayList<StepFilter.StackFrameSnapshot>();

	public StepFilter(ThreadId thread, StepSize size, StepDepth depth, Iterator<StackFrame> stackFrameIterator) throws InvalidThreadException {
		if (thread.getReference().get() == null) {
			throw new InvalidThreadException(thread.getId());
		}

		this.thread = thread;
		this.size = size;
		this.depth = depth;

		while (stackFrameIterator.hasNext()) {
			StackFrame frame = stackFrameIterator.next();
			if (!frame.isSynthetic()) {
				new StackFrameSnapshot(frame);
			}

		}

	}

	/**
	 * Calculates size of a stack from the given {@link StackFrame} instance.
	 * 
	 * @param stackFrame
	 *            The stack frame from where to start the calculation.
	 * @return the calculated size.
	 */
	private int stackSize(StackFrame stackFrame) {
		int depth = 1;
		while (previousNonSyntheticStackFrame(stackFrame) != null) {
			stackFrame = previousNonSyntheticStackFrame(stackFrame);
			++depth;
		}
		return depth;
	}

	/**
	 * Lookups previous non-synthetic frame for the given {@link StackFrame}
	 * instance.
	 * 
	 * @param stackFrame
	 *            The stack frame from where to start the calculation.
	 * @return
	 */
	private StackFrame previousNonSyntheticStackFrame(StackFrame stackFrame) {
		while (stackFrame.getPrevious() != null) {
			stackFrame = stackFrame.getPrevious();
			if (!stackFrame.isSynthetic()) {
				return stackFrame;
			}
		}
		return null;
	}

	/**
	 * Does the given event match the filter?
	 * 
	 * @param event
	 *            the <code>Event</code> to scrutinize
	 */
	public boolean matches(SingleStepEvent event) {

		Instruction currentInstruction = event.getLocation().getInstruction();

		ThreadInfo currentThread = event.getThread();

		/* Are we in the right thread? */
		if (VMIdManager.getDefault().getObjectId(currentThread) != thread) {
			return false;
		}

		StackFrame currentStackFrame = currentThread.getLastNonSyntheticStackFrame();
		int currentStackFrameSize = stackSize(currentStackFrame);

		/* If we're in a synthetic method, return immediately */
		if (currentInstruction != currentStackFrame.getPC()) {
			log.finest("Skip encountered for a synthetic instruction (or instruction from a synthetic frame): " + currentInstruction + ", (fileresource: "
					+ currentInstruction.getFileLocation() + ")");
			return false;
		}

		log.finest("Current instruction: " + currentInstruction + ", current frame: " + currentStackFrame + " (filesource: "
				+ currentInstruction.getFileLocation() + "), stack size: " + currentStackFrameSize);

		switch (size) {
		case LINE:
			switch (depth) {
			case INTO:

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
						 * synthetic method where we won't be able to step in.
						 * 
						 * Let's do not care about this...
						 */
						return true;
					}

				}

				break;
			case OUT:

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
				break;

			case OVER:

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

				break;
			default:
				// TODO Error handling
				throw new RuntimeException("dead block reached");
				// TODO remove this
			}

			break;
		case MIN:
			if (stackSnapshot.get(0).pc != currentInstruction) {
				return true;
			}
			break;
		default:
			// TODO Error handling
			throw new RuntimeException("dead block reached"); // TODO remove
																// this
		}

		return false;
	}

	/**
	 * Compares line from the stack at the given depth to the line of the given
	 * instruction.
	 * 
	 * @param snapshotStackDepth
	 *            The stack depth where to look for a line number.
	 * @param instruction
	 *            Comparison reference instruction.
	 * @return Whether given instruction when compared to the instruction at the
	 *         given stack depth is located at a different line.
	 */
	private boolean lineDiffers(int snapshotStackDepth, Instruction instruction) {
		return stackSnapshot.get(snapshotStackDepth).pc.getLineNumber() != instruction.getLineNumber();
	}

	/**
	 * @see StepFilter#lineDiffers(int, Instruction)
	 * 
	 * @param instruction
	 *            Comparison reference instruction.
	 * @return Whether given instruction when compared to the instruction at the
	 *         top of the stack is located at a different line.
	 */
	private boolean currentLineDiffers(Instruction instruction) {
		return lineDiffers(0, instruction);
	}

}
