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

import gov.nasa.jpf.jdwp.VirtualMachine.CapabilitiesNew;
import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.VirtualMachineHelper.MethodResult;
import gov.nasa.jpf.jdwp.exception.IllegalArgumentException;
import gov.nasa.jpf.jdwp.exception.JdwpException;
import gov.nasa.jpf.jdwp.exception.ThreadNotSuspendedException;
import gov.nasa.jpf.jdwp.exception.id.InvalidFieldIdException;
import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.jdwp.value.ValueUtils;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.DynamicElementInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.Monitor;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ObjectReferenceCommand} enum class implements the
 * {@link CommandSet#OBJECTREFERENCE} set of commands. For the detailed
 * specification refer to <a href=
 * "http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#JDWP_ObjectReference"
 * >http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#
 * JDWP_ObjectReference</a> JDWP 1.6 Specification pages.
 * 
 * @author stepan
 * 
 */
public enum ObjectReferenceCommand implements Command, ConvertibleEnum<Byte, ObjectReferenceCommand> {

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the runtime type of the object. The runtime type will be a class or
   * an array.
   * </p>
   */
  REFERENCETYPE(1) {
    @Override
    public void execute(ObjectId objectId, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      ElementInfo elementInfo = objectId.get();

      ClassInfo classInfo = elementInfo.getClassInfo();

      ReferenceTypeId refId = contextProvider.getObjectManager().getReferenceTypeId(classInfo);
      refId.writeTagged(os);
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the value of one or more instance fields. Each field must be member
   * of the object's type or one of its superclasses, superinterfaces, or
   * implemented interfaces. Access control is not enforced; for example, the
   * values of private fields can be obtained.
   * </p>
   */
  GETVALUES(2) {
    @Override
    public void execute(ObjectId objectId, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      ElementInfo obj = (DynamicElementInfo) objectId.get();
      int fields = bytes.getInt();

      os.writeInt(fields);

      for (int i = 0; i < fields; i++) {
        FieldId fieldId = contextProvider.getObjectManager().readFieldId(bytes);
        FieldInfo field = fieldId.get();

        ClassInfo classInfo = field.getClassInfo();
        if (!obj.getClassInfo().isInstanceOf(classInfo)) {
          // this is here just for completeness
          // it's not required since fieldId doesn't need classInfo to resolve
          throw new InvalidFieldIdException(fieldId);
        }

        Value value = ValueUtils.fieldToValue(obj, field);
        value.writeTagged(os);
      }

    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Sets the value of one or more instance fields. Each field must be member of
   * the object's type or one of its superclasses, superinterfaces, or
   * implemented interfaces. Access control is not enforced; for example, the
   * values of private fields can be set. For primitive values, the value's type
   * must match the field's type exactly. For object values, there must be a
   * widening reference conversion from the value's type to the field's type and
   * the field's type must be loaded.
   * </p>
   */
  SETVALUES(3) {
    @Override
    public void execute(ObjectId objectId, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      ElementInfo obj = objectId.getModifiable();
      int values = bytes.getInt();

      for (int i = 0; i < values; ++i) {
        FieldId fieldId = contextProvider.getObjectManager().readFieldId(bytes);
        FieldInfo fieldInfo = fieldId.get();

        ClassInfo classInfo = fieldInfo.getClassInfo();
        if (!obj.getClassInfo().isInstanceOf(classInfo)) {
          // this is here just for completeness
          // it's not required since fieldId doesn't need classInfo to resolve
          throw new InvalidFieldIdException(fieldId);
        }

        ClassInfo fieldClassInfo = fieldInfo.getTypeClassInfo();
        Tag tag = Tag.classInfoToTag(fieldClassInfo);
        Value valueUntagged = tag.readValue(bytes);

        // set the value into the object's field
        valueUntagged.modify(obj, fieldInfo);
      }

    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns monitor information for an object. All threads int the VM must be
   * suspended.
   * </p>
   * 
   * Requires {@link CapabilitiesNew#CAN_GET_MONITOR_INFO} capability.
   */
  MONITORINFO(5) {
    @Override
    public void execute(ObjectId objectId, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      ElementInfo elementInfo = objectId.get();
      Monitor monitor = elementInfo.getMonitor();

      // The monitor owner, or null if it is not currently owned.
      ThreadInfo ownerThreadInfo = monitor.getLockingThread();
      ThreadId ownerThreadId = contextProvider.getObjectManager().getThreadId(ownerThreadInfo);
      ownerThreadId.write(os);

      // The number of times the monitor has been entered.
      int entryCount = monitor.getLockCount();
      os.writeInt(entryCount);

      // The number of threads that are waiting for the monitor 0 if there
      // is no current owner
      int waiters = monitor.getNumberOfWaitingThreads();
      os.writeInt(waiters);

      for (ThreadInfo waitingThread : monitor.getWaitingThreads()) {
        // A thread waiting for this monitor.
        ThreadId threadId = contextProvider.getObjectManager().getThreadId(waitingThread);
        threadId.write(os);
      }
    }
  },

  /**
   * <h2>JDWP Specification</h2>
   * <p>
   * Invokes a instance method. The method must be member of the object's type
   * or one of its superclasses, superinterfaces, or implemented interfaces.
   * Access control is not enforced; for example, private methods can be
   * invoked.
   * </p>
   * <p>
   * The method invocation will occur in the specified thread. Method invocation
   * can occur only if the specified thread has been suspended by an event.
   * Method invocation is not supported when the target VM has been suspended by
   * the front-end.
   * </p>
   * <p>
   * The specified method is invoked with the arguments in the specified
   * argument list. The method invocation is synchronous; the reply packet is
   * not sent until the invoked method returns in the target VM. The return
   * value (possibly the void value) is included in the reply packet. If the
   * invoked method throws an exception, the exception object ID is set in the
   * reply packet; otherwise, the exception object ID is null.
   * </p>
   * <p>
   * For primitive arguments, the argument value's type must match the
   * argument's type exactly. For object arguments, there must be a widening
   * reference conversion from the argument value's type to the argument's type
   * and the argument's type must be loaded.
   * </p>
   * <p>
   * By default, all threads in the target VM are resumed while the method is
   * being invoked if they were previously suspended by an event or by command.
   * This is done to prevent the deadlocks that will occur if any of the threads
   * own monitors that will be needed by the invoked method. It is possible that
   * breakpoints or other events might occur during the invocation. Note,
   * however, that this implicit resume acts exactly like the ThreadReference
   * resume command, so if the thread's suspend count is greater than 1, it will
   * remain in a suspended state during the invocation. By default, when the
   * invocation completes, all threads in the target VM are suspended,
   * regardless their state before the invocation.
   * </p>
   * <p>
   * The resumption of other threads during the invoke can be prevented by
   * specifying the INVOKE_SINGLE_THREADED bit flag in the options field;
   * however, there is no protection against or recovery from the deadlocks
   * described above, so this option should be used with great caution. Only the
   * specified thread will be resumed (as described for all threads above). Upon
   * completion of a single threaded invoke, the invoking thread will be
   * suspended once again. Note that any threads started during the single
   * threaded invocation will not be suspended when the invocation completes.
   * </p>
   * <p>
   * If the target VM is disconnected during the invoke (for example, through
   * the VirtualMachine dispose command) the method invocation continues.
   * </p>
   */
  INVOKEMETHOD(6) {
    @Override
    public void execute(ObjectId objectId, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      ThreadId threadId = contextProvider.getObjectManager().readThreadId(bytes);
      ReferenceTypeId clazz = contextProvider.getObjectManager().readReferenceTypeId(bytes);
      MethodInfo methodInfo = VirtualMachineHelper.getClassMethod(clazz.get(), bytes.getLong());
      int arguments = bytes.getInt();
      Value[] values = new Value[arguments];
      for (int i = 0; i < arguments; ++i) {
        values[i] = Tag.bytesToValue(bytes);
      }
      int options = bytes.getInt();

      ThreadInfo thread = threadId.getThreadInfo();
      if (!contextProvider.getVirtualMachine().getExecutionManager().isThreadSuspended(thread)) {
        throw new ThreadNotSuspendedException("Thread not suspended: " + thread);
      }

      MethodResult methodResult = VirtualMachineHelper.invokeMethod(objectId.get(), methodInfo, values, thread, options);
      methodResult.write(os);
    }

  },

  /**
   * <h2>JDWP Specification</h2>
   * <p>
   * Prevents garbage collection for the given object. By default all objects in
   * back-end replies may be collected at any time the target VM is running. A
   * call to this command guarantees that the object will not be collected. The
   * {@link ObjectReferenceCommand#ENABLECOLLECTION} command can be used to
   * allow collection once again.
   * </p>
   * <p>
   * Note that while the target VM is suspended, no garbage collection will
   * occur because all threads are suspended. The typical examination of
   * variables, fields, and arrays during the suspension is safe without
   * explicitly disabling garbage collection.
   * </p>
   * <p>
   * This method should be used sparingly, as it alters the pattern of garbage
   * collection in the target VM and, consequently, may result in application
   * behavior under the debugger that differs from its non-debugged behavior.
   * </p>
   */
  DISABLECOLLECTION(7) {
    @Override
    public void execute(ObjectId objectId, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      contextProvider.getVirtualMachine().disableCollection(objectId);
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Permits garbage collection for this object. By default all objects returned
   * by JDWP may become unreachable in the target VM, and hence may be garbage
   * collected. A call to this command is necessary only if garbage collection
   * was previously disabled with the
   * {@link ObjectReferenceCommand#DISABLECOLLECTION} command.
   * </p>
   */
  ENABLECOLLECTION(8) {
    @Override
    public void execute(ObjectId objectId, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      contextProvider.getVirtualMachine().enableCollection(objectId);
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Determines whether an object has been garbage collected in the target VM.
   * </p>
   */
  ISCOLLECTED(9) {
    @Override
    public void execute(ObjectId objectId, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      boolean isCollected = objectId.isNull();
      logger.debug("Is collect: {}", isCollected);
      os.writeBoolean(isCollected);
    }
  },

  /**
   * Returns objects that directly reference this object. Only objects that are
   * reachable for the purposes of garbage collection are returned. Note that an
   * object can also be referenced in other ways, such as from a local variable
   * in a stack frame, or from a JNI global reference. Such non-object referrers
   * are not returned by this command. <br/>
   * Requires {@link CapabilitiesNew#CAN_GET_INSTANCE_INFO} capability
   * 
   * @since JDWP version 1.6.
   */
  REFERRINGOBJECTS(10) {

    @Override
    public void execute(ObjectId objectId, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {

      int maxReferrers = bytes.getInt();

      if (maxReferrers < 0) {
        throw new IllegalArgumentException("The max referrers cannot be negative: " + maxReferrers);
      }

      Set<ObjectId> referringObjectRefs = VirtualMachineHelper.getReferringObjects(objectId.get().getObjectRef(), maxReferrers,
                                                                                   contextProvider);

      // write the results
      os.writeInt(referringObjectRefs.size());
      for (ObjectId referringObjectId : referringObjectRefs) {
        referringObjectId.writeTagged(os);
      }

    }

  };

  final static Logger logger = LoggerFactory.getLogger(ObjectReferenceCommand.class);

  private byte commandId;

  private ObjectReferenceCommand(int commandId) {
    this.commandId = (byte) commandId;
  }

  private static ReverseEnumMap<Byte, ObjectReferenceCommand> map = new ReverseEnumMap<Byte, ObjectReferenceCommand>(
      ObjectReferenceCommand.class);

  @Override
  public Byte identifier() {
    return commandId;
  }

  @Override
  public ObjectReferenceCommand convert(Byte val) throws IllegalArgumentException {
    return map.get(val);
  }

  @Override
  public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
    ObjectId objectId = contextProvider.getObjectManager().readObjectId(bytes);

    logger.debug("Object ID: {}", objectId);
    execute(objectId, bytes, os, contextProvider);
  }

  /**
   * The {@link ObjectReferenceCommand} specific extension of command execution.
   * 
   * @param objectId
   *          The object that is associated with the object reference commands.
   * @param bytes
   *          The buffer of bytes that is used as an input of the command.
   * @param os
   *          The output stream that is used for a command output.
   * @param contextProvider
   *          The Context Provider.
   * @throws IOException
   *           If given input or output have I/O issues.
   * @throws JdwpException
   *           If any JDWP based error occurs.
   */
  public abstract void execute(ObjectId objectId, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException;

}