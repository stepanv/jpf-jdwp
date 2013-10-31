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

package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.JdwpConstants;
import gov.nasa.jpf.jdwp.VirtualMachine.CapabilitiesNew;
import gov.nasa.jpf.jdwp.VirtualMachine.ExecutionManager;
import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.exception.IllegalArgumentException;
import gov.nasa.jpf.jdwp.exception.JdwpException;
import gov.nasa.jpf.jdwp.exception.JdwpException.ErrorType;
import gov.nasa.jpf.jdwp.exception.NotImplementedException;
import gov.nasa.jpf.jdwp.exception.ThreadNotSuspendedException;
import gov.nasa.jpf.jdwp.exception.id.object.InvalidObjectException;
import gov.nasa.jpf.jdwp.exception.id.object.InvalidThreadException;
import gov.nasa.jpf.jdwp.id.FrameId;
import gov.nasa.jpf.jdwp.id.JdwpIdManager;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadGroupId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.id.object.ThreadId.SuspendStatus;
import gov.nasa.jpf.jdwp.id.object.ThreadId.ThreadStatus;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.jdwp.value.JdwpString;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.DynamicElementInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Heap;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ThreadReferenceCommand} enum class implements the
 * {@link CommandSet#THREADREFERENCE} set of commands. For the detailed
 * specification refer to <a href=
 * "http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#JDWP_ThreadReference"
 * >http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#
 * JDWP_ThreadReference</a> JDWP 1.6 Specification pages.
 * 
 * @author stepan
 * 
 */
public enum ThreadReferenceCommand implements Command, ConvertibleEnum<Byte, ThreadReferenceCommand> {

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the thread name.
   * </p>
   */
  NAME(1) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      JdwpString.write(threadInfo.getName(), os);
    }
  },

  /**
   * 
   * <h2>JDWP Specification</h2>
   * <p>
   * Suspends the thread.
   * </p>
   * <p>
   * Unlike {@link java.lang.Thread#suspend()}, suspends of both the virtual
   * machine and individual threads are counted. Before a thread will run again,
   * it must be resumed the same number of times it has been suspended.
   * </p>
   * <p>
   * Suspending single threads with command has the same dangers
   * {@link java.lang.Thread#suspend()}. If the suspended thread holds a monitor
   * needed by another running thread, deadlock is possible in the target VM (at
   * least until the suspended thread is resumed again).
   * </p>
   * <p>
   * The suspended thread is guaranteed to remain suspended until resumed
   * through one of the JDI resume methods mentioned above; the application in
   * the target VM cannot resume the suspended thread through
   * {@link java.lang.Thread#resume()}.
   * </p>
   * <p>
   * Note that this doesn't change the status of the thread (see the
   * {@link ThreadReferenceCommand#STATUS} command.) For example, if it was
   * Running, it will still appear running to other threads.
   * </p>
   * 
   * <h2>JPF specifics</h2>
   * <p>
   * Note that when a single thread in JPF is suspended by the debugger no other
   * thread is running. For more information refer to {@link ExecutionManager}.
   * </p>
   */
  SUSPEND(2) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      contextProvider.getVirtualMachine().getExecutionManager().markThreadSuspended(threadInfo);
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Resumes the execution of a given thread. If this thread was not previously
   * suspended by the front-end, calling this command has no effect. Otherwise,
   * the count of pending suspends on this thread is decremented. If it is
   * decremented to 0, the thread will continue to execute.
   * </p>
   * 
   * <p>
   * <h2>JPF specifics</h2>
   * Note that when a single thread in JPF is suspended by the debugger no other
   * thread is running. For more information refer to {@link ExecutionManager}.
   * </p>
   */
  RESUME(3) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      contextProvider.getVirtualMachine().getExecutionManager().markThreadResumed(threadInfo);
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the current status of a thread. The thread status reply indicates
   * the thread status the last time it was running. the suspend status provides
   * information on the thread's suspension, if any.
   * </p>
   */
  STATUS(4) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      ThreadStatus threadStatus = threadStatus(threadInfo);

      os.writeInt(threadStatus.identifier());

      // This is how it is implemented in Harmony and OpenJDK (0 if not
      // suspended); however, the JDWP specification isn't clear about
      // this.
      int suspendStatus = 0;
      if (contextProvider.getVirtualMachine().getExecutionManager().isThreadSuspended(threadInfo)) {
        // There's only one possible SuspendStatus...
        suspendStatus = SuspendStatus.SUSPEND_STATUS_SUSPENDED.identifier();
      }

      os.writeInt(suspendStatus);
      logger.debug("status: {}, suspend status: {}", threadStatus, suspendStatus);

    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the thread group that contains a given thread.
   * </p>
   */
  THREADGROUP(5) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      int group = threadInfo.getThreadObject().getReferenceField(JdwpConstants.FIELDNAME_THREAD_GROUP);
      ElementInfo ei = contextProvider.getVM().getHeap().get(group);
      ThreadGroupId groupId = contextProvider.getObjectManager().getThreadGroupId(ei);
      groupId.write(os);
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the current call stack of a suspended thread. The sequence of
   * frames starts with the currently executing frame, followed by its caller,
   * and so on. The thread must be suspended, and the returned frameID is valid
   * only while the thread is suspended.
   * </p>
   * <p>
   * Note that even though the specification doesn't say what to do if the
   * thread is not suspended it's probably implied that the same behavior as
   * with {@link ThreadReferenceCommand#FRAMECOUNT} is expected.<br/>
   * Therefore, returns {@link ErrorType#THREAD_NOT_SUSPENDED} if not suspended.
   * </p>
   */
  FRAMES(6) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {

      if (!contextProvider.getVirtualMachine().getExecutionManager().isThreadSuspended(threadInfo)) {
        // even though the specification says nothing about what to do
        // if the thread is not suspended we have to do something!
        throw new ThreadNotSuspendedException();
      }

      int startFrame = bytes.getInt();
      int length = bytes.getInt();

      List<StackFrame> frames = VirtualMachineHelper.getFrames(threadInfo, startFrame, length);
      os.writeInt(frames.size());
      for (int i = 0; i < frames.size(); i++) {
        StackFrame frame = (StackFrame) frames.get(i);

        FrameId frameId = contextProvider.getObjectManager().getFrameId(threadInfo, i);
        frameId.write(os);

        Location location = Location.factorySafe(frame.getPC(), threadInfo);
        location.write(os);

        logger.debug("Frame: {}, StackFrame {}, Location: {}", frameId, frame, location);
      }

    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the count of frames on this thread's stack. The thread must be
   * suspended, and the returned count is valid only while the thread is
   * suspended. <br/>
   * Returns {@link ErrorType#THREAD_NOT_SUSPENDED} if not suspended.
   * </p>
   */
  FRAMECOUNT(7) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {

      if (!contextProvider.getVirtualMachine().getExecutionManager().isThreadSuspended(threadInfo)) {
        throw new ThreadNotSuspendedException();
      }

      int frameCount = VirtualMachineHelper.getFrameCount(threadInfo);
      os.writeInt(frameCount);

      logger.debug("writing frame count: {}", frameCount);

    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the objects whose monitors have been entered by this thread. The
   * thread must be suspended, and the returned information is relevant only
   * while the thread is suspended.
   * </p>
   * Requires {@link CapabilitiesNew#CAN_GET_OWNED_MONITOR_INFO} capability.
   * <p>
   * Note that even though the specification doesn't say what to do if the
   * thread is not suspended it's probably implied that the same behavior as
   * with {@link ThreadReferenceCommand#FRAMECOUNT} is expected.<br/>
   * Therefore, returns {@link ErrorType#THREAD_NOT_SUSPENDED} if not suspended.
   * </p>
   */
  OWNEDMONITORS(8) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {

      if (!contextProvider.getVirtualMachine().getExecutionManager().isThreadSuspended(threadInfo)) {
        // even though the specification says nothing about what to do
        // if the thread is not suspended we have to do something!
        throw new ThreadNotSuspendedException();
      }

      JdwpIdManager objectManager = contextProvider.getObjectManager();
      Heap heap = contextProvider.getVM().getHeap();

      // write number of owned monitors
      os.writeInt(threadInfo.getLockedObjectReferences().length);

      for (int objRef : threadInfo.getLockedObjectReferences()) {
        ElementInfo elementInfo = heap.get(objRef);
        ObjectId objectId = objectManager.getObjectId(elementInfo);
        objectId.writeTagged(os);
      }
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the object, if any, for which this thread is waiting. The thread
   * may be waiting to enter a monitor, or it may be waiting, via the
   * java.lang.Object.wait method, for another thread to invoke the notify
   * method. The thread must be suspended, and the returned information is
   * relevant only while the thread is suspended.
   * </p>
   * Requires {@link CapabilitiesNew#CAN_GET_CURRENT_CONTENDED_MONITOR}
   * capability.
   * <p>
   * Note that even though the specification doesn't say what to do if the
   * thread is not suspended it's probably implied that the same behavior as
   * with {@link ThreadReferenceCommand#FRAMECOUNT} is expected.<br/>
   * Therefore, returns {@link ErrorType#THREAD_NOT_SUSPENDED} if not suspended.
   * </p>
   */
  CURRENTCONTENDEDMONITOR(9) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {

      if (!contextProvider.getVirtualMachine().getExecutionManager().isThreadSuspended(threadInfo)) {
        // even though the specification says nothing about what to do
        // if the thread is not suspended we have to do something!
        throw new ThreadNotSuspendedException();
      }

      // the thread was suspended by the debugger
      ElementInfo lockObject = threadInfo.getLockObject();

      // null lock object if no lock - will be NullObjectId
      ObjectId objectId = contextProvider.getObjectManager().getObjectId(lockObject);
      objectId.writeTagged(os);

    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Stops the thread with an asynchronous exception, as if done by
   * {@link java.lang.Thread#stop()}.
   * </p>
   */
  STOP(10) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      ObjectId exceptionId = contextProvider.getObjectManager().readObjectId(bytes);
      ClassInfo exceptionClass = exceptionId.get().getClassInfo();

      if (!exceptionClass.isInstanceOf("java.lang.Throwable")) {
        throw new InvalidObjectException(exceptionId);
      }

      threadInfo.throwException(exceptionId.get().getObjectRef());
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Interrupt the thread, as if done by {@link java.lang.Thread#interrupt()}.
   * </p>
   */
  INTERRUPT(11) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      MethodInfo method = threadInfo.getClassInfo().getMethod("interrupt()V", true);

      VirtualMachineHelper.invokeMethod((DynamicElementInfo) threadInfo.getThreadObject(), method, new Value[] {}, threadInfo, 0);
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Get the suspend count for this thread. The suspend count is the number of
   * times the thread has been suspended through the thread-level or VM-level
   * suspend commands without a corresponding resume.
   * </p>
   */
  SUSPENDCOUNT(12) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {

      int suspendCount = contextProvider.getVirtualMachine().getExecutionManager().suspendCount(threadInfo);
      logger.debug("Suspend count: {}", suspendCount);
      os.writeInt(suspendCount);
    }

  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns monitor objects owned by the thread, along with stack depth at
   * which the monitor was acquired. Returns stack depth of -1 if the
   * implementation cannot determine the stack depth (e.g., for monitors
   * acquired by JNI MonitorEnter).The thread must be suspended, and the
   * returned information is relevant only while the thread is suspended.
   * </p>
   * Requires {@link CapabilitiesNew#CAN_GET_MONITOR_FRAME_INFO} capability.
   * 
   * @since JDWP version 1.6.
   * 
   */
  OWNEDMONITORSSTACKDEPTHINFO(13) {

    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      // it's ok as far as the associated capability is false
      throw new NotImplementedException();
    }

  },

  /**
   * <h2>JDWP Specification</h2>
   * <p>
   * Force a method to return before it reaches a return statement.
   * </p>
   * <p>
   * The method which will return early is referred to as the called method. The
   * called method is the current method (as defined by the Frames section in
   * the Java Virtual Machine Specification) for the specified thread at the
   * time this command is received.
   * </p>
   * <p>
   * The specified thread must be suspended. The return occurs when execution of
   * Java programming language code is resumed on this thread. Between sending
   * this command and resumption of thread execution, the state of the stack is
   * undefined.
   * </p>
   * <p>
   * No further instructions are executed in the called method. Specifically,
   * finally blocks are not executed. Note: this can cause inconsistent states
   * in the application.
   * </p>
   * <p>
   * A lock acquired by calling the called method (if it is a synchronized
   * method) and locks acquired by entering synchronized blocks within the
   * called method are released. Note: this does not apply to JNI locks or
   * java.util.concurrent.locks locks.
   * </p>
   * <p>
   * Events, such as MethodExit, are generated as they would be in a normal
   * return.
   * </p>
   * <p>
   * The called method must be a non-native Java programming language method.
   * Forcing return on a thread with only one frame on the stack causes the
   * thread to exit when resumed.
   * </p>
   * <p>
   * For void methods, the value must be a void value. For methods that return
   * primitive values, the value's type must match the return type exactly. For
   * object values, there must be a widening reference conversion from the
   * value's type to the return type type and the return type must be loaded.
   * </p>
   * Requires {@link CapabilitiesNew#CAN_FORCE_EARLY_RETURN} capability.
   * 
   * @since JDWP version 1.6.
   */
  FORCEEARLYRETURN(14) {

    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      // it's ok as far as the associated capability is false
      throw new NotImplementedException();
    }

  };

  final static Logger logger = LoggerFactory.getLogger(ThreadReferenceCommand.class);
  private byte commandId;

  private ThreadReferenceCommand(int commandId) {
    this.commandId = (byte) commandId;
  }

  private static ReverseEnumMap<Byte, ThreadReferenceCommand> map = new ReverseEnumMap<Byte, ThreadReferenceCommand>(
                                                                                                                     ThreadReferenceCommand.class);

  @Override
  public Byte identifier() {
    return commandId;
  }

  @Override
  public ThreadReferenceCommand convert(Byte val) throws IllegalArgumentException {
    return map.get(val);
  }

  /**
   * JPF Thread statuses to JDWP Thread statuses mapping
   * 
   * @param threadInfo
   *          The thread
   * @return The JDWP status of the thread
   */

  private static ThreadStatus threadStatus(ThreadInfo threadInfo) {

    switch (threadInfo.getState()) {
    case BLOCKED:
      return ThreadStatus.MONITOR;

    case TIMEOUT_WAITING:
    case NOTIFIED:
    case INTERRUPTED:
    case TIMEDOUT:
    case WAITING:
      return ThreadStatus.WAIT;

    case TERMINATED:
      return ThreadStatus.ZOMBIE;

    case SLEEPING:
      return ThreadStatus.SLEEPING;

    case UNBLOCKED:
    case RUNNING:
    default:
      return ThreadStatus.RUNNING;
    }
  }

  protected abstract void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException;

  @Override
  public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
    ThreadId threadId = contextProvider.getObjectManager().readThreadId(bytes);

    ThreadInfo threadInfo = threadId.getThreadInfo();

    logger.debug("Thread ID: {}, ThreadInfo: {}", threadId, threadInfo);

    if (threadInfo.isTerminated()) {
      throw new InvalidThreadException(threadId);
    }

    execute(threadInfo, bytes, os, contextProvider);
  }
}
