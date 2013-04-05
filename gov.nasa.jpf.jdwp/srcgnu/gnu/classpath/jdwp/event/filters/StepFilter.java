/* StepFilter.java -- a step filter
   Copyright (C) 2005, 2007 Free Software Foundation

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */

package gnu.classpath.jdwp.event.filters;

import gnu.classpath.jdwp.JdwpConstants;
import gnu.classpath.jdwp.VMIdManager;
import gnu.classpath.jdwp.event.Event;
import gnu.classpath.jdwp.event.SingleStepEvent;
import gnu.classpath.jdwp.exception.InvalidThreadException;
import gnu.classpath.jdwp.id.ThreadId;
import gov.nasa.jpf.jvm.bytecode.EXECUTENATIVE;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * "An event filter which restricts reported step events to those which satisfy
 * depth and size constraints. This modifier can only be used with step event
 * kinds."
 * 
 * This "filter" is not really a filter. It is simply a way to communicate
 * stepping information in a convenient way between the JDWP backend and the
 * virtual machine.
 * 
 * Consequently, this "filter" always matches.
 * 
 * @author Keith Seitz (keiths@redhat.com)
 */
public class StepFilter implements IEventFilter {
	private ThreadId _tid;
	private int _size;
	private int _depth;
	private List<StackFrameSnapshot> stackSnapshot = new ArrayList<StepFilter.StackFrameSnapshot>();

	/**
	 * Constructs a new StepFilter
	 * 
	 * @param tid
	 *            ID of the thread in which to step
	 * @param size
	 *            size of each step
	 * @param depth
	 *            relative call stack limit
	 * @param instruction
	 * @throws InvalidThreadException
	 *             if thread is invalid
	 */
	public StepFilter(ThreadId tid, int size, int depth, Iterator<StackFrame> stackFrameIterator) throws InvalidThreadException {
		if (tid.getReference().get() == null)
			throw new InvalidThreadException(tid.getId());

		_tid = tid;
		_size = size;
		_depth = depth;

		while (stackFrameIterator.hasNext()) {
			StackFrame frame = stackFrameIterator.next();
			if (!frame.isSynthetic()) {
				new StackFrameSnapshot(frame);
			}

		}

	}

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

			System.out.println("STEP FILTER: Adding frame snapshot of " + frame + ", instruction: " + pc + " (location: " + pc.getFileLocation() + ")");

			stackSnapshot.add(this);
		}
	}

	/**
	 * Returns the thread in which to step
	 * 
	 * @return the thread's ID
	 */
	public ThreadId getThread() {
		return _tid;
	}

	/**
	 * Returns the size of each step (insn, line)
	 * 
	 * @return the step size
	 * @see gnu.classpath.jdwp.JdwpConstants.StepSize
	 */
	public int getSize() {
		return _size;
	}

	/**
	 * Returns the relative call stack limit (into, over, out)
	 * 
	 * @return how to step
	 * @see gnu.classpath.jdwp.JdwpConstants.StepDepth
	 */
	public int getDepth() {
		return _depth;
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
	public boolean matches(Event event) // TODO need to restrict this to
										// StepEvent only (polymorphism and use
										// generics)
	{
		if (event instanceof SingleStepEvent) {
			SingleStepEvent stepEvent = (SingleStepEvent) event;

			Instruction currentInstruction = stepEvent.getLocation().getInstruction();

			ThreadInfo currentThread = ((SingleStepEvent) event).getThread();

			/* Are we in the right thread? */
			if (VMIdManager.getDefault().getObjectId(currentThread) != _tid) {
				return false;
			}

			StackFrame currentStackFrame = currentThread.getLastNonSyntheticStackFrame();
			int currentStackFrameSize = stackSize(currentStackFrame);

			/* If we're in a synthetic method, return immediately */
			if (currentInstruction != currentStackFrame.getPC()) {
				System.out.println("STEP FILTER SKIP for a synthetic instruction (or instruction from a synthetic frame): " + currentInstruction
						+ ", (fileresource: " + currentInstruction.getFileLocation() + ")");
				return false;
			}

			System.out.println("STEP FILTER: current instruction: " + currentInstruction + ", current frame: " + currentStackFrame + " (filesource: "
					+ currentInstruction.getFileLocation() + "), stack size: " + currentStackFrameSize);

			switch (_size) {
			case JdwpConstants.StepSize.LINE:
				switch (_depth) {
				case JdwpConstants.StepDepth.INTO:

					/* we just stepped in some method */
					if (currentStackFrameSize > stackSnapshot.size()) {

						/*
						 * we're accepting only method very beginnings and we
						 * don't step into native methods
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
						 * we're about to enter another method at the same line
						 * from where our method was invoked
						 */
						if (currentInstruction instanceof InvokeInstruction) {

							/*
							 * this might be tricky because instance of
							 * InvokeInstruction could also invoke just a native
							 * or synthetic method where we won't be able to
							 * step in.
							 * 
							 * Let's do not care about this...
							 */
							return true;
						}

					}

					break;
				case JdwpConstants.StepDepth.OUT:

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

				case JdwpConstants.StepDepth.OVER:

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
			case JdwpConstants.StepSize.MIN:
				if (stackSnapshot.get(0).pc != currentInstruction) {
					return true;
				}
				break;
			default:
				// TODO Error handling
				throw new RuntimeException("dead block reached"); // TODO remove
																	// this
			}

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
	 * @param instruction Comparison reference instruction.
	 * @return Whether given instruction when compared to the instruction at the
	 *         top of the stack is located at a different line.
	 */
	private boolean currentLineDiffers(Instruction instruction) {
		return lineDiffers(0, instruction);
	}
}
