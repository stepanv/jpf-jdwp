package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.JdwpObjectManager;
import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.VirtualMachineHelper.MethodResult;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.MethodInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum ClassTypeCommand implements Command, ConvertibleEnum<Byte, ClassTypeCommand> {
	SUPERCLASS(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ReferenceTypeId refId = contextProvider.getObjectManager().readReferenceTypeId(bytes);
			ClassInfo clazz = refId.get();
			ClassInfo superClazz = clazz.getSuperClass();

			if (superClazz == null) {
				os.writeLong(0L);
			} else {
				ReferenceTypeId clazzId = contextProvider.getObjectManager().getReferenceTypeId(superClazz);
				clazzId.write(os);
			}

		}
	},
	SETVALUES(2) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

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
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ReferenceTypeId clazz = JdwpObjectManager.getInstance().readReferenceTypeId(bytes);
			ThreadId threadId = JdwpObjectManager.getInstance().readThreadId(bytes);
			MethodInfo method = VirtualMachineHelper.getClassMethod(clazz.get(), bytes.getLong());

			int arguments = bytes.getInt();
			Value[] values = new Value[arguments];

			for (int i = 0; i < arguments; i++) {
				values[i] = Tag.bytesToValue(bytes);
			}

			int options = bytes.getInt();

			MethodResult methodResult = VirtualMachineHelper.invokeMethod(null, method, values, threadId.get());

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
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ReferenceTypeId clazz = JdwpObjectManager.getInstance().readReferenceTypeId(bytes);
			ThreadId threadId = JdwpObjectManager.getInstance().readThreadId(bytes);
			MethodInfo method = VirtualMachineHelper.getClassMethod(clazz.get(), bytes.getLong());

			int arguments = bytes.getInt();
			Value[] values = new Value[arguments];

			for (int i = 0; i < arguments; i++) {
				values[i] = Tag.bytesToValue(bytes);
			}

			int options = bytes.getInt();

			MethodResult methodResult = VirtualMachineHelper.invokeMethod(null, method, values, threadId.get(), true);

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
	public abstract void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;

	// private MethodResult invokeMethod(ByteBuffer bytes)
	// throws JdwpError, IOException
	// {
	// ReferenceTypeId refId =
	// JdwpObjectManager.getInstance().readReferenceTypeId(bytes);
	// ClassInfo clazz = refId.get();
	//
	// ThreadId tId = JdwpObjectManager.getInstance().readThreadId(bytes);
	// ThreadInfo thread = tId.get();
	//
	// MethodInfo method = VirtualMachineHelper.getClassMethod(clazz,
	// bytes.getLong());
	//
	// int args = bytes.getInt();
	// Value[] values = new Value[args];
	//
	// for (int i = 0; i < args; i++) {
	// values[i] = Tag.bytesToValue(bytes);
	// }
	//
	// int invokeOpts = bytes.getInt();
	// //throw new RuntimeException("not implemented");
	// MethodResult mr = VMVirtualMachine.executeMethod(null, thread,
	// clazz, method,
	// values, invokeOpts);
	// return mr;
	// }
}