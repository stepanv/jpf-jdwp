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

import gov.nasa.jpf.jdwp.command.CommandContextProvider;
import gov.nasa.jpf.jdwp.command.ConvertibleEnum;
import gov.nasa.jpf.jdwp.command.ReverseEnumMap;
import gov.nasa.jpf.jdwp.event.StepFilterable;
import gov.nasa.jpf.jdwp.exception.IllegalArgumentException;
import gov.nasa.jpf.jdwp.exception.id.object.InvalidObjectException;
import gov.nasa.jpf.jdwp.exception.id.object.InvalidThreadException;
import gov.nasa.jpf.jdwp.id.JdwpIdManager;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * 
 * 
 * <p>
 * <h2>JDWP Specification</h2>
 * Restricts reported step events to those which satisfy depth and size
 * constraints. This modifier can be used with step event kinds only.
 * </p>
 * 
 * @see StepIntoFilter
 * @see StepOutFilter
 * @see StepOverFilter
 * 
 * @author stepan
 * 
 */
public abstract class StepFilter extends Filter<StepFilterable> {

  private static Logger log = Logger.getLogger(StepFilter.class.getName());

  public static enum StepSize implements ConvertibleEnum<Integer, StepSize> {

    /** Step by the minimum possible amount (often a bytecode instruction). */
    MIN(0),

    /**
     * Step to the next source line unless there is no line number information
     * in which case a MIN step is done instead.
     */
    LINE(1);

    private int stepSizeId;

    private StepSize(int stepSizeId) {
      this.stepSizeId = stepSizeId;
    }

    @Override
    public Integer identifier() {
      return stepSizeId;
    }

    private static ReverseEnumMap<Integer, StepSize> map = new ReverseEnumMap<Integer, StepFilter.StepSize>(StepSize.class);

    @Override
    public StepSize convert(Integer stepSizeId) throws IllegalArgumentException {
      return map.get(stepSizeId);
    }

  }

  public static enum StepDepth implements ConvertibleEnum<Integer, StepDepth> {

    /** Step into any method calls that occur before the end of the step. */
    INTO(0) {
      @Override
      public StepFilter createStepFilter(ThreadId threadId, StepSize stepSize, CommandContextProvider contextProvider) throws InvalidThreadException, InvalidObjectException {
        return new StepIntoFilter(threadId, stepSize);
      }
    },

    /** Step over any method calls that occur before the end of the step. */
    OVER(1) {
      @Override
      public StepFilter createStepFilter(ThreadId threadId, StepSize stepSize, CommandContextProvider contextProvider) throws InvalidThreadException, InvalidObjectException {
        return new StepOverFilter(threadId, stepSize);
      }
    },

    /** Step out of the current method. */
    OUT(2) {
      @Override
      public StepFilter createStepFilter(ThreadId threadId, StepSize stepSize, CommandContextProvider contextProvider) throws InvalidThreadException, InvalidObjectException {
        return new StepOutFilter(threadId, stepSize);
      }
    };

    private int stepDepthId;

    private StepDepth(int stepDepthId) {
      this.stepDepthId = stepDepthId;
    }

    @Override
    public Integer identifier() {
      return stepDepthId;
    }

    private static ReverseEnumMap<Integer, StepDepth> map = new ReverseEnumMap<Integer, StepDepth>(StepDepth.class);

    @Override
    public StepDepth convert(Integer stepDepthId) throws IllegalArgumentException {
      return map.get(stepDepthId);
    }

    /**
     * Creates the specific step filter.
     * 
     * @param threadId
     *          The thread associated with this filter.
     * @param stepSize
     *          The step size.
     * @param contextProvider
     *          The context provider.
     * @return The {@link StepFilter} subtype instance.
     * @throws InvalidThreadException
     *           If given thread ID is not an ID of a thread.
     * @throws InvalidObjectException
     *           If given object ID is not valid or has been GCed.
     */
    public abstract StepFilter createStepFilter(ThreadId threadId, StepSize stepSize, CommandContextProvider contextProvider) throws InvalidThreadException, InvalidObjectException;

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

  /**
   * 
   * @param threadId
   *          Thread in which to step
   * @param size
   *          Size of each step
   * @throws InvalidThreadException
   * @throws InvalidObjectException
   */
  public StepFilter(ThreadId threadId, StepSize size) throws InvalidThreadException, InvalidObjectException {
    super(ModKind.STEP, StepFilterable.class);

    if (threadId.isNull() || threadId.get() == null) {
      throw new InvalidThreadException(threadId);
    }

    this.thread = threadId;
    this.size = size;

    Iterator<StackFrame> stackFrameIterator = threadId.getThreadInfo().iterator();

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
   *          The stack frame from where to start the calculation.
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
   *          The stack frame from where to start the calculation.
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
  public boolean matches(StepFilterable event) throws InvalidObjectException {
    return matches(event.getLocation().getInstruction(), event.getThread());
  }

  /**
   * The core algorithm of the step filtering.
   * 
   * @param currentInstruction
   *          The instruction to match.
   * @param currentThread
   *          The thread to match
   * @return Whether matched.
   */
  private boolean matches(Instruction currentInstruction, ThreadInfo currentThread) {

    /* Are we in the right thread? */
    if (JdwpIdManager.getInstance().getThreadId(currentThread) != thread) {
      return false;
    }

    StackFrame currentStackFrame = currentThread.getLastNonSyntheticStackFrame();
    if (currentStackFrame == null) {
      /* There is no way to go */
      return false;
    }
    int currentStackFrameSize = stackSize(currentStackFrame);

    /* If we're in a synthetic method, return immediately */
    if (currentInstruction != currentStackFrame.getPC()) {
      log.finest("Skip encountered for a synthetic instruction (or instruction from a synthetic frame): " + currentInstruction
          + ", (fileresource: " + currentInstruction.getFileLocation() + ")");
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
      // the provided size is invalid hence it doesn't match
      return false;
    }

    return false;
  }

  /**
   * Compares line from the stack at the given depth to the line of the given
   * instruction.
   * 
   * @param snapshotStackDepth
   *          The stack depth where to look for a line number.
   * @param instruction
   *          Comparison reference instruction.
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
   *          Comparison reference instruction.
   * @return Whether given instruction when compared to the instruction at the
   *         top of the stack is located at a different line.
   */
  protected boolean currentLineDiffers(Instruction instruction) {
    return lineDiffers(0, instruction);
  }

  protected abstract boolean matches(int currentStackFrameSize, Instruction currentInstruction);

  /**
   * Creates the {@link StepFilter} subtype that implements the actual
   * filtering.
   * 
   * @param bytes
   *          The buffer of bytes to create the step filter from.
   * @param contextProvider
   *          The Context provider.
   * @return The newly created {@link StepFilter} instance.
   * @throws IllegalArgumentException
   *           If the sent depth of size are invalid.
   * @throws InvalidObjectException
   *           If any of the sent objects are invalid.
   * @throws InvalidThreadException
   *           If given thread ID is not an ID of a thread.
   */
  public static StepFilter factory(ByteBuffer bytes, CommandContextProvider contextProvider) throws IllegalArgumentException, InvalidThreadException, InvalidObjectException {
    ThreadId threadId = contextProvider.getObjectManager().readThreadId(bytes);
    int size = bytes.getInt();
    int depth = bytes.getInt();

    return StepDepth.INTO.convert(depth).createStepFilter(threadId, StepSize.LINE.convert(size), contextProvider);
  }

}
