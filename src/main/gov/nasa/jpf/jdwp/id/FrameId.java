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

package gov.nasa.jpf.jdwp.id;

import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.exception.id.InvalidFrameIdException;
import gov.nasa.jpf.jdwp.exception.id.InvalidIdentifierException;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

/**
 * This class implements the corresponding <code>frameID</code> common data type
 * from the JDWP Specification.
 * 
 * <p>
 * Even though JPF introduces synthetic frames, the JDWP back-end must filter
 * them since JPDA does not support synthetic attribute for the frames.
 * </p>
 * 
 * <p>
 * <h2>Frame ID</h2>
 * According to the specification the frame ID must be unique not just for a
 * thread but for the whole VM. On the other hand it needs to be valid only
 * during the time the associated thread is suspended.<br/>
 * Therefore, the ID is computed from
 * <ul>
 * <li>the Thread's object reference ID which is a SGOID, hence is unique and
 * also the same whenever the same thread is constructed even when the state
 * space is backtracked and then executed along a different path.</li>
 * <li>and the frame's depth in the thread's call stack which is by definition
 * unique per a thread.</li>
 * </ul>
 * The similar implementation is used by the OpenJDK as well as Harmony JVM
 * project.
 * </p>
 * 
 * <p>
 * <h2>JDWP Specification</h2>
 * Uniquely identifies a frame in the target VM. The frameID must uniquely
 * identify the frame within the entire VM (not only within a given thread). The
 * frameID need only be valid during the time its thread is suspended.
 * </p>
 * 
 * @author stepan
 * @see VirtualMachineHelper#getFrame(ThreadInfo, int)
 * @see VirtualMachineHelper#getFrames(ThreadInfo, int, int)
 * 
 */
public class FrameId extends IdentifierBase<StackFrame> {

  private int threadRef;
  private int depth;

  /**
   * Frame ID constructor for creating the frame IDs for the given thread and
   * it's call stack depth.
   * 
   * @param threadInfo
   *          The thread to create the frame for.
   * @param depth
   *          The depth of the frame of the given thread's call stack.
   */
  FrameId(ThreadInfo threadInfo, int depth) {
    this(threadInfo.getThreadObjectRef(), depth);
  }

  /**
   * Frame ID constructor for getting the frames from the specified id.<br/>
   * Even though this constructor can be technically called with any
   * <code>id</code> only IDs created by the
   * {@link FrameId#FrameId(ThreadInfo, int)} constructor are supposed to be
   * used. Therefore, this constructor should be used for subsequent call after
   * the standard constructor is called.
   * 
   * @param id
   *          The frame ID.
   */
  FrameId(long id) {
    this(toThreadRef(id), toDepth(id));
  }

  /**
   * Internal Frame ID constructor.
   * 
   * @param threadRef
   *          The JPF VM heap object reference of a thread.
   * @param depth
   *          The depth of the frame of the thread associated with the given
   *          thread reference this frame ID is constructed for.
   */
  private FrameId(int threadRef, int depth) {
    super(toId(threadRef, depth), null);
    this.threadRef = threadRef;
    this.depth = depth;
  }

  /**
   * Translates the frame's unique attributes into the frame ID.
   * 
   * @param threadRef
   *          The thread reference that together with the depth uniquely
   *          identify a frame.
   * @param depth
   *          The thread's call stack depth that together with the thread
   *          reference uniquely identify a frame.
   * @return The frame ID.
   */
  private static long toId(int threadRef, int depth) {
    return (long) threadRef << 32 | (long) depth;
  }

  /**
   * Translates the frame ID into a thread reference.
   * 
   * @param id
   *          The frame ID.
   * @return The associated thread reference.
   */
  private static int toThreadRef(long id) {
    return (int) (id >> 32);
  }

  /**
   * Translates the frame ID into a thread's call stack depth.
   * 
   * @param id
   *          The frame ID.
   * @return The associated thread's call stack depth.
   */
  private static int toDepth(long id) {
    return (int) id;
  }

  @Override
  public StackFrame get() throws InvalidIdentifierException {
    ThreadInfo thread = VM.getVM().getThreadList().getThreadInfoForObjRef(threadRef);
    if (thread == null) {
      // the thread encoded in the id is not known (can be terminated too)
      throw new InvalidFrameIdException(id());
    }

    StackFrame frame = VirtualMachineHelper.getFrame(thread, depth);
    if (frame == null) {
      // there is no frame at the provided depth.
      throw new InvalidFrameIdException(id());
    }
    return frame;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nasa.jpf.jdwp.id.Identifier#nullObjectHandler(gov.nasa.jpf.jdwp.id.
   * Identifier)
   */
  @Override
  public StackFrame nullObjectHandler() throws InvalidFrameIdException {
    throw new UnsupportedOperationException("This method should not be needed!");
  }

}
