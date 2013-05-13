package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.JdwpObjectManager;
import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.VirtualMachineHelper.MethodResult;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.id.object.special.NullObjectId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.MethodInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum ClassTypeCommand implements Command, ConvertibleEnum<Byte, ClassTypeCommand> {
	SUPERCLASS(1) {
		@Override
		public void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ClassInfo superClassInfo = classInfo.getSuperClass();

			if (superClassInfo == null) {
				NullObjectId.getInstance().write(os);
			} else {
				ReferenceTypeId referenceTypeId = contextProvider.getObjectManager().getReferenceTypeId(superClassInfo);
				referenceTypeId.write(os);
			}

		}
	},
	/**
	 * <p>
	 * <h2>JDWP Specification</h2>
	 * Sets the value of one or more static fields. Each field must be member of
	 * the class type or one of its superclasses, superinterfaces, or
	 * implemented interfaces. Access control is not enforced; for example, the
	 * values of private fields can be set. Final fields cannot be set.For
	 * primitive values, the value's type must match the field's type exactly.
	 * For object values, there must exist a widening reference conversion from
	 * the value's type to the field's type and the field's type must be loaded.
	 * </p>
	 */
	SETVALUES(2) {
		@Override
		public void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			int values = bytes.getInt();
			
			ElementInfo staticElementInfo = classInfo.getStaticElementInfo();
			
			for (int i = 0; i < values; ++i) {
				FieldId fieldId = contextProvider.getObjectManager().readFieldId(bytes);
				FieldInfo fieldInfo = fieldId.get();
				ClassInfo fieldClassInfo = fieldInfo.getTypeClassInfo();
				Tag tag = Tag.classInfoToTag(fieldClassInfo);
				Value valueUntagged = tag.readValue(bytes);
				
				valueUntagged.modify(staticElementInfo.getFields(), fieldInfo.getFieldIndex());
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
	 * The method invocation will occur in the specified thread. Method
	 * invocation can occur only if the specified thread has been suspended by
	 * an event. Method invocation is not supported when the target VM has been
	 * suspended by the front-end.
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
	 * reference conversion from the argument value's type to the argument's
	 * type and the argument's type must be loaded.
	 * </p>
	 * <p>
	 * By default, all threads in the target VM are resumed while the method is
	 * being invoked if they were previously suspended by an event or by
	 * command. This is done to prevent the deadlocks that will occur if any of
	 * the threads own monitors that will be needed by the invoked method. It is
	 * possible that breakpoints or other events might occur during the
	 * invocation. Note, however, that this implicit resume acts exactly like
	 * the ThreadReference resume command, so if the thread's suspend count is
	 * greater than 1, it will remain in a suspended state during the
	 * invocation. By default, when the invocation completes, all threads in the
	 * target VM are suspended, regardless their state before the invocation.
	 * </p>
	 * <p>
	 * The resumption of other threads during the invoke can be prevented by
	 * specifying the INVOKE_SINGLE_THREADED bit flag in the options field;
	 * however, there is no protection against or recovery from the deadlocks
	 * described above, so this option should be used with great caution. Only
	 * the specified thread will be resumed (as described for all threads
	 * above). Upon completion of a single threaded invoke, the invoking thread
	 * will be suspended once again. Note that any threads started during the
	 * single threaded invocation will not be suspended when the invocation
	 * completes.
	 * </p>
	 * <p>
	 * If the target VM is disconnected during the invoke (for example, through
	 * the VirtualMachine dispose command) the method invocation continues.
	 * </p>
	 */
	INVOKEMETHOD(3) {
		@Override
		public void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ThreadId threadId = JdwpObjectManager.getInstance().readThreadId(bytes);
			MethodInfo method = VirtualMachineHelper.getClassMethod(classInfo, bytes.getLong());

			int arguments = bytes.getInt();
			Value[] values = new Value[arguments];

			for (int i = 0; i < arguments; i++) {
				values[i] = Tag.bytesToValue(bytes);
			}

			int options = bytes.getInt();

			MethodResult methodResult = VirtualMachineHelper.invokeMethod(null, method, values, threadId.getInfoObject(), options);

			methodResult.write(os);

		}
	},

	/**
	 * <p>
	 * <h2>JDWP Specification:</h2>
	 * Creates a new object of this type, invoking the specified constructor.
	 * The constructor method ID must be a member of the class type.
	 * </p>
	 * <p>
	 * Instance creation will occur in the specified thread. Instance creation
	 * can occur only if the specified thread has been suspended by an event.
	 * Method invocation is not supported when the target VM has been suspended
	 * by the front-end.
	 * </p>
	 * <p>
	 * The specified constructor is invoked with the arguments in the specified
	 * argument list. The constructor invocation is synchronous; the reply
	 * packet is not sent until the invoked method returns in the target VM. The
	 * return value (possibly the void value) is included in the reply packet.
	 * If the constructor throws an exception, the exception object ID is set in
	 * the reply packet; otherwise, the exception object ID is null.
	 * </p>
	 * <p>
	 * For primitive arguments, the argument value's type must match the
	 * argument's type exactly. For object arguments, there must exist a
	 * widening reference conversion from the argument value's type to the
	 * argument's type and the argument's type must be loaded.
	 * </p>
	 * <p>
	 * By default, all threads in the target VM are resumed while the method is
	 * being invoked if they were previously suspended by an event or by
	 * command. This is done to prevent the deadlocks that will occur if any of
	 * the threads own monitors that will be needed by the invoked method. It is
	 * possible that breakpoints or other events might occur during the
	 * invocation. Note, however, that this implicit resume acts exactly like
	 * the ThreadReference resume command, so if the thread's suspend count is
	 * greater than 1, it will remain in a suspended state during the
	 * invocation. By default, when the invocation completes, all threads in the
	 * target VM are suspended, regardless their state before the invocation.
	 * </p>
	 * <p>
	 * The resumption of other threads during the invoke can be prevented by
	 * specifying the INVOKE_SINGLE_THREADED bit flag in the options field;
	 * however, there is no protection against or recovery from the deadlocks
	 * described above, so this option should be used with great caution. Only
	 * the specified thread will be resumed (as described for all threads
	 * above). Upon completion of a single threaded invoke, the invoking thread
	 * will be suspended once again. Note that any threads started during the
	 * single threaded invocation will not be suspended when the invocation
	 * completes.
	 * </p>
	 * <p>
	 * If the target VM is disconnected during the invoke (for example, through
	 * the VirtualMachine dispose command) the method invocation continues.
	 * </p>
	 */
	NEWINSTANCE(4) {
		@Override
		public void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ThreadId threadId = JdwpObjectManager.getInstance().readThreadId(bytes);
			MethodInfo method = VirtualMachineHelper.getClassMethod(classInfo, bytes.getLong());

			int arguments = bytes.getInt();
			Value[] values = new Value[arguments];

			for (int i = 0; i < arguments; i++) {
				values[i] = Tag.bytesToValue(bytes);
			}

			int options = bytes.getInt();

			MethodResult methodResult = VirtualMachineHelper.invokeConstructor(method, values, threadId.getInfoObject(), options);

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
	public ClassTypeCommand convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
		ReferenceTypeId referenceTypeId = contextProvider.getObjectManager().readReferenceTypeId(bytes);
		execute(referenceTypeId.get(), bytes, os, contextProvider);
	}

	public abstract void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;
	
}