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
import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.exception.InvalidThreadException;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.exception.ThreadNotSuspended;
import gov.nasa.jpf.jdwp.id.FrameId;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadGroupId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.id.object.ThreadId.SuspendStatus;
import gov.nasa.jpf.jdwp.id.object.ThreadId.ThreadStatus;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.jdwp.value.JdwpString;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Heap;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ThreadReferenceCommand implements Command, ConvertibleEnum<Byte, ThreadReferenceCommand> {
  NAME(1) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {
      JdwpString.write(threadInfo.getName(), os);
    }
  },
  SUSPEND(2) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {
      contextProvider.getVirtualMachine().getExecutionManager().markThreadSuspended(threadInfo);
    }
  },
  RESUME(3) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {
      contextProvider.getVirtualMachine().getExecutionManager().markThreadResumed(threadInfo);
    }
  },
  STATUS(4) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {
      ThreadStatus threadStatus = threadStatus(threadInfo);

      os.writeInt(threadStatus.identifier());

      // This is how it is implemented in Harmony and OpenJDK (0 if not
      // suspended)
      // Although the JDWP specification isn't clear about this
      int suspendStatus = 0;
      if (contextProvider.getVirtualMachine().getExecutionManager().suspendCount(threadInfo) > 0) {
        // There's only one possible SuspendStatus...
        suspendStatus = SuspendStatus.SUSPEND_STATUS_SUSPENDED.identifier();
      }

      os.writeInt(suspendStatus);
      logger.debug("status: {}, suspend status: {}", threadStatus, suspendStatus);

    }
  },
  THREADGROUP(5) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {
      int group = threadInfo.getThreadObject().getReferenceField(JdwpConstants.FIELDNAME_THREAD_GROUP);
      ElementInfo ei = contextProvider.getVM().getHeap().get(group);
      ThreadGroupId groupId = contextProvider.getObjectManager().getThreadGroupId(ei);
      groupId.write(os);
    }
  },

  /**
   * Returns the current call stack of a suspended thread. The sequence of
   * frames starts with the currently executing frame, followed by its caller,
   * and so on. The thread must be suspended, and the returned frameID is valid
   * only while the thread is suspended.
   */
  FRAMES(6) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {

      if (contextProvider.getVirtualMachine().getExecutionManager().suspendCount(threadInfo) <= 0) {
        // the specification isn't clear about this though
        throw new ThreadNotSuspended();
      }

      int startFrame = bytes.getInt();
      int length = bytes.getInt();

      List<StackFrame> frames = VirtualMachineHelper.getFrames(threadInfo, startFrame, length);
      os.writeInt(frames.size());
      for (int i = 0; i < frames.size(); i++) {
        StackFrame frame = (StackFrame) frames.get(i);

        FrameId frameId = contextProvider.getObjectManager().getFrameId(frame);
        frameId.write(os);

        Location location = Location.factorySafe(frame.getPC(), threadInfo);
        location.write(os);

        logger.debug("Frame: {}, StackFrame {}, Location: {}", frameId, frame, location);
      }

    }
  },
  FRAMECOUNT(7) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {

      if (contextProvider.getVirtualMachine().getExecutionManager().suspendCount(threadInfo) <= 0) {
        // the specification isn't clear about this though
        throw new ThreadNotSuspended();
      }

      int frameCount = VirtualMachineHelper.getFrameCount(threadInfo);
      os.writeInt(frameCount);

      logger.debug("writing frame count: {}", frameCount);

    }
  },
  /**
   * Returns the objects whose monitors have been entered by this thread. The
   * thread must be suspended, and the returned information is relevant only
   * while the thread is suspended. Requires
   * {@link CapabilitiesNew#CAN_GET_OWNED_MONITOR_INFO} capability.
   */
  OWNEDMONITORS(8) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {
      // TODO verify the thread is suspended - we need to redesign thread
      // suspension

      JdwpObjectManager objectManager = contextProvider.getObjectManager();
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
   * Returns the object, if any, for which this thread is waiting. The thread
   * may be waiting to enter a monitor, or it may be waiting, via the
   * java.lang.Object.wait method, for another thread to invoke the notify
   * method. The thread must be suspended, and the returned information is
   * relevant only while the thread is suspended. Requires
   * {@link CapabilitiesNew#CAN_GET_CURRENT_CONTENDED_MONITOR} capability.
   */
  CURRENTCONTENDEDMONITOR(9) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {
      // TODO verify the thread is suspended - we need to redesign thread
      // suspension

      ObjectId objectId = contextProvider.getObjectManager().getObjectId(threadInfo.getLockObject());
      objectId.writeTagged(os);
    }
  },
  STOP(10) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {
      throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

    }
  },
  INTERRUPT(11) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {
      throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

    }
  },
  SUSPENDCOUNT(12) {
    @Override
    protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {

      int suspendCount = contextProvider.getVirtualMachine().getExecutionManager().suspendCount(threadInfo);
      logger.debug("Suspend count: {}", suspendCount);
      os.writeInt(suspendCount);
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
  public ThreadReferenceCommand convert(Byte val) throws JdwpError {
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

  protected abstract void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
      throws IOException, JdwpError;

  @Override
  public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
    ThreadId threadId = contextProvider.getObjectManager().readThreadId(bytes);

    ThreadInfo threadInfo = threadId.getInfoObject();

    logger.debug("Thread ID: {}, ThreadInfo: {}", threadId, threadInfo);

    if (threadInfo.isTerminated()) {
      throw new InvalidThreadException(threadId);
    }

    execute(threadInfo, bytes, os, contextProvider);
  }
}
