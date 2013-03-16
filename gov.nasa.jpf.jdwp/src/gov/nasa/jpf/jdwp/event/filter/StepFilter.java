package gov.nasa.jpf.jdwp.event.filter;

import gnu.classpath.jdwp.VMIdManager;
import gnu.classpath.jdwp.exception.InvalidThreadException;
import gnu.classpath.jdwp.id.ThreadId;
import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.event.EventRequest;
import gov.nasa.jpf.jdwp.event.SingleStepEvent;
import gov.nasa.jpf.jdwp.event.Event.EventKind;
import gov.nasa.jpf.jdwp.exception.IllegalArgumentException;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;

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
public abstract class StepFilter extends Filter<SingleStepEvent> {

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

	protected List<StackFrameSnapshot> stackSnapshot = new ArrayList<StepFilter.StackFrameSnapshot>();

	public StepFilter(ThreadId thread, StepSize size, Iterator<StackFrame> stackFrameIterator) throws InvalidThreadException {
		super(ModKind.STEP);

		if (thread.getReference().get() == null) {
			throw new InvalidThreadException(thread.getId());
		}

		this.thread = thread;
		this.size = size;

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

	@Override
	public boolean matches(SingleStepEvent event) {

		Instruction currentInstruction = event.getLocation().getInstruction();
		ThreadInfo currentThread = event.getThread().get();

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
			return matches(currentStackFrameSize, currentInstruction);

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
	protected boolean lineDiffers(int snapshotStackDepth, Instruction instruction) {
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
	protected boolean currentLineDiffers(Instruction instruction) {
		return lineDiffers(0, instruction);
	}

	protected abstract boolean matches(int currentStackFrameSize, Instruction currentInstruction);
	
	@Override
	public boolean isAllowedEventKind(EventKind eventKind) {
		switch(eventKind) {
		case SINGLE_STEP:
			return true;
		default:
			return false;
		}
	}


}
