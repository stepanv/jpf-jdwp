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

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.id.FrameId;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.StaticElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum StackFrameCommand implements Command, ConvertibleEnum<Byte, StackFrameCommand> {
  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the value of one or more local variables in a given frame. Each
   * variable must be visible at the frame's code index. Even if local variable
   * information is not available, values can be retrieved if the front-end is
   * able to determine the correct local variable index. (Typically, this index
   * can be determined for method arguments from the method signature without
   * access to the local variable table information.)
   * </p>
   */
  GETVALUES(1) {
    @Override
    public void execute(ThreadInfo threadInfo, StackFrame stackFrame, ByteBuffer bytes, DataOutputStream os,
                        CommandContextProvider contextProvider) throws IOException, JdwpError {
      int slots = bytes.getInt();
      os.writeInt(slots);

      for (int i = 0; i < slots; i++) {
        int slot = bytes.getInt();
        byte tag = bytes.get();

        Object object = null;

        LocalVarInfo localVarInfo = stackFrame.getLocalVarInfo(slot);

        // object will remain as null (if it really is null)
        object = stackFrame.getLocalValueObject(localVarInfo);

        Value value = Tag.taggedObjectToValue(tag, object);
        value.writeTagged(os);

      }
    }
  },
  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Sets the value of one or more local variables. Each variable must be
   * visible at the current frame code index. For primitive values, the value's
   * type must match the variable's type exactly. For object values, there must
   * be a widening reference conversion from the value's type to the variable's
   * type and the variable's type must be loaded.
   * <p>
   * </p>
   * Even if local variable information is not available, values can be set, if
   * the front-end is able to determine the correct local variable index.
   * (Typically, this index can be determined for method arguments from the
   * method signature without access to the local variable table information.)
   * </p>
   */
  SETVALUES(2) {
    @Override
    public void execute(ThreadInfo threadInfo, StackFrame stackFrame, ByteBuffer bytes, DataOutputStream os,
                        CommandContextProvider contextProvider) throws JdwpError {
      int slotValues = bytes.getInt();

      StackFrame stackFrameModifiable = threadInfo.getModifiableFrame(stackFrame);

      for (int i = 0; i < slotValues; ++i) {
        int slot = bytes.getInt();

        Value value = Tag.bytesToValue(bytes);
        value.modify(stackFrameModifiable, slot);
      }
    }
  },
  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the value of the 'this' reference for this frame. If the frame's
   * method is static or native, the reply will contain the null object
   * reference.
   * </p>
   */
  THISOBJECT(3) {
    @Override
    public void execute(ThreadInfo threadInfo, StackFrame stackFrame, ByteBuffer bytes, DataOutputStream os,
                        CommandContextProvider contextProvider) throws IOException {
      ElementInfo thisObject = VM.getVM().getHeap().get(stackFrame.getThis());

      if (thisObject instanceof StaticElementInfo) {
        // TODO this is possibly completely wrong
        throw new IllegalArgumentException("Not sure whether we're allowed to return static elements");
      }
      ObjectId thisObjectId = contextProvider.getObjectManager().getObjectId(thisObject);
      logger.debug("Found this object id: {}, object itself: {}", thisObjectId, thisObject);
      thisObjectId.writeTagged(os);
    }
  },
  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Pop the top-most stack frames of the thread stack, up to, and including
   * 'frame'. The thread must be suspended to perform this command. The top-most
   * stack frames are discarded and the stack frame previous to 'frame' becomes
   * the current frame. The operand stack is restored -- the argument values are
   * added back and if the invoke was not <code>invokestatic</code>,
   * <code>objectref</code> is added back as well. The Java virtual machine
   * program counter is restored to the opcode of the invoke instruction.
   * <p>
   * </p>
   * Since JDWP version 1.4. Requires canPopFrames capability - see
   * {@link VirtualMachineCommand#CAPABILITIESNEW}. </p>
   */
  POPFRAMES(4) {
    @Override
    public void execute(ThreadInfo threadInfo, StackFrame stackFrame, ByteBuffer bytes, DataOutputStream os,
                        CommandContextProvider contextProvider) {

      for (StackFrame frame = threadInfo.getTopFrame(); (frame != null) && (frame != stackFrame); frame = frame.getPrevious()) {
        threadInfo.leave(); // that takes care of releasing locks
        threadInfo.popFrame();
      }
    }
  };

  final static Logger logger = LoggerFactory.getLogger(StackFrameCommand.class);

  @Override
  public Byte identifier() {
    return commandId;
  }

  @Override
  public StackFrameCommand convert(Byte val) throws JdwpError {
    return map.get(val);
  }

  private byte commandId;

  private StackFrameCommand(int commandId) {
    this.commandId = (byte) commandId;
  }

  private static ReverseEnumMap<Byte, StackFrameCommand> map = new ReverseEnumMap<Byte, StackFrameCommand>(StackFrameCommand.class);

  @Override
  public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws JdwpError, IOException {
    ThreadId threadId = contextProvider.getObjectManager().readThreadId(bytes);
    FrameId frameId = contextProvider.getObjectManager().readFrameId(bytes);

    // TODO frameId.get() should return InvalidFrame instead of
    // InvalidObject
    execute(threadId.getInfoObject(), frameId.get(), bytes, os, contextProvider);
  }

  public abstract void execute(ThreadInfo threadInfo, StackFrame stackFrame, ByteBuffer bytes, DataOutputStream os,
                               CommandContextProvider contextProvider) throws IOException, JdwpError;

}