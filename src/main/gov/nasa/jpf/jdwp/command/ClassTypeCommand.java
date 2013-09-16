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

import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.VirtualMachineHelper.MethodResult;
import gov.nasa.jpf.jdwp.exception.IllegalArgumentException;
import gov.nasa.jpf.jdwp.exception.JdwpException;
import gov.nasa.jpf.jdwp.exception.ThreadNotSuspendedException;
import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.JdwpIdManager;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * The {@link ClassTypeCommand} enum class implements the
 * {@link CommandSet#CLASSTYPE} set of commands. For the detailed specification
 * refer to <a href=
 * "http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#JDWP_ClassObjectReference"
 * >http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#
 * JDWP_ClassObjectReference</a> JDWP 1.6 Specification pages.
 * 
 * @author stepan
 * 
 */
public enum ClassTypeCommand implements Command, ConvertibleEnum<Byte, ClassTypeCommand> {

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the immediate superclass of a class.
   * </p>
   */
  SUPERCLASS(1) {
    @Override
    public void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      ClassInfo superClassInfo = classInfo.getSuperClass();
      
      // if classInfo is java.lang.Object, then NullReference is returned
      ReferenceTypeId referenceTypeId = contextProvider.getObjectManager().getReferenceTypeId(superClassInfo);
      referenceTypeId.write(os);

    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Sets the value of one or more static fields. Each field must be member of
   * the class type or one of its superclasses, superinterfaces, or implemented
   * interfaces. Access control is not enforced; for example, the values of
   * private fields can be set. Final fields cannot be set.For primitive values,
   * the value's type must match the field's type exactly. For object values,
   * there must exist a widening reference conversion from the value's type to
   * the field's type and the field's type must be loaded.
   * </p>
   */
  SETVALUES(2) {
    @Override
    public void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      int values = bytes.getInt();

      ElementInfo staticElementInfo = classInfo.getModifiableStaticElementInfo();

      for (int i = 0; i < values; ++i) {
        FieldId fieldId = contextProvider.getObjectManager().readFieldId(bytes);
        FieldInfo fieldInfo = fieldId.get();
        ClassInfo fieldClassInfo = fieldInfo.getTypeClassInfo();
        Tag tag = Tag.classInfoToTag(fieldClassInfo);
        Value valueUntagged = tag.readValue(bytes);

        valueUntagged.modify(staticElementInfo, fieldInfo);
      }
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
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
  INVOKEMETHOD(3) {
    @Override
    public void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      ThreadId threadId = JdwpIdManager.getInstance().readThreadId(bytes);
      MethodInfo method = VirtualMachineHelper.getClassMethod(classInfo, bytes.getLong());

      int arguments = bytes.getInt();
      Value[] values = new Value[arguments];

      for (int i = 0; i < arguments; i++) {
        values[i] = Tag.bytesToValue(bytes);
      }

      int options = bytes.getInt();

      ThreadInfo thread = threadId.getThreadInfo();
      if (!contextProvider.getVirtualMachine().getExecutionManager().isThreadSuspended(thread)) {
        throw new ThreadNotSuspendedException("Thread not suspended: " + thread);
      }

      MethodResult methodResult = VirtualMachineHelper.invokeMethod(null, method, values, thread, options);

      methodResult.write(os);

    }
  },

  /**
   * <p>
   * <h2>JDWP Specification:</h2>
   * Creates a new object of this type, invoking the specified constructor. The
   * constructor method ID must be a member of the class type.
   * </p>
   * <p>
   * Instance creation will occur in the specified thread. Instance creation can
   * occur only if the specified thread has been suspended by an event. Method
   * invocation is not supported when the target VM has been suspended by the
   * front-end.
   * </p>
   * <p>
   * The specified constructor is invoked with the arguments in the specified
   * argument list. The constructor invocation is synchronous; the reply packet
   * is not sent until the invoked method returns in the target VM. The return
   * value (possibly the void value) is included in the reply packet. If the
   * constructor throws an exception, the exception object ID is set in the
   * reply packet; otherwise, the exception object ID is null.
   * </p>
   * <p>
   * For primitive arguments, the argument value's type must match the
   * argument's type exactly. For object arguments, there must exist a widening
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
  NEWINSTANCE(4) {
    @Override
    public void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      ThreadId threadId = JdwpIdManager.getInstance().readThreadId(bytes);
      MethodInfo method = VirtualMachineHelper.getClassMethod(classInfo, bytes.getLong());

      int arguments = bytes.getInt();
      Value[] values = new Value[arguments];

      for (int i = 0; i < arguments; i++) {
        values[i] = Tag.bytesToValue(bytes);
      }

      int options = bytes.getInt();

      ThreadInfo thread = threadId.getThreadInfo();
      if (!contextProvider.getVirtualMachine().getExecutionManager().isThreadSuspended(thread)) {
        throw new ThreadNotSuspendedException("Thread not suspended: " + thread);
      }

      MethodResult methodResult = VirtualMachineHelper.invokeConstructor(method, values, thread, options);

      methodResult.write(os);

    }
  };

  private byte commandId;

  private ClassTypeCommand(int commandId) {
    this.commandId = (byte) commandId;
  }

  private static ReverseEnumMap<Byte, ClassTypeCommand> map = new ReverseEnumMap<Byte, ClassTypeCommand>(ClassTypeCommand.class);

  @Override
  public Byte identifier() {
    return commandId;
  }

  @Override
  public ClassTypeCommand convert(Byte val) throws IllegalArgumentException {
    return map.get(val);
  }

  @Override
  public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
    ReferenceTypeId classTypeId = contextProvider.getObjectManager().readClassTypeId(bytes);
    execute(classTypeId.get(), bytes, os, contextProvider);
  }

  public abstract void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException;

}