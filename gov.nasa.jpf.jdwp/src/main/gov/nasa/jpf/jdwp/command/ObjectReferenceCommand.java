package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.VirtualMachine.CapabilitiesNew;
import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.VirtualMachineHelper.MethodResult;
import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.id.object.special.NullObjectId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jdwp.value.Value;
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

public enum ObjectReferenceCommand implements Command, ConvertibleEnum<Byte, ObjectReferenceCommand> {
	REFERENCETYPE(1) {
		@Override
		public void execute(ObjectId objectId, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ElementInfo elementInfo = objectId.get();
			
			// TODO solve ObjectId#get() == null everywhere !!!
			if (elementInfo == null) {
				throw new InvalidObject("Object is null: " + objectId);
			}
			ClassInfo classInfo = elementInfo.getClassInfo();
			
			ReferenceTypeId refId = contextProvider.getObjectManager().getReferenceTypeId(classInfo);
			refId.writeTagged(os);
		}
	},
	/**
	 * Returns the value of one or more instance fields. Each field must be
	 * member of the object's type or one of its superclasses, superinterfaces,
	 * or implemented interfaces. Access control is not enforced; for example,
	 * the values of private fields can be obtained.
	 */
	GETVALUES(2) {
		@Override
		public void execute(ObjectId objectId, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ElementInfo obj = (DynamicElementInfo) objectId.get();
			int fields = bytes.getInt();

			os.writeInt(fields);

			for (int i = 0; i < fields; i++) {
				FieldInfo field = contextProvider.getObjectManager().readFieldId(bytes).get();
				System.out.println(field);
				// field.setAccessible(true); // Might be a private field
				Object object = field.getValueObject(obj.getFields());
				Value val = Tag.classInfoToTag(field.getTypeClassInfo()).value(object);
				val.writeTagged(os);
			}

		}
	},

	/**
	 * Sets the value of one or more instance fields. Each field must be member
	 * of the object's type or one of its superclasses, superinterfaces, or
	 * implemented interfaces. Access control is not enforced; for example, the
	 * values of private fields can be set. For primitive values, the value's
	 * type must match the field's type exactly. For object values, there must
	 * be a widening reference conversion from the value's type to the field's
	 * type and the field's type must be loaded.
	 */
	SETVALUES(3) {
		@Override
		public void execute(ObjectId objectId, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ElementInfo obj = (DynamicElementInfo) objectId.get();
			int values = bytes.getInt();

			for (int i = 0; i < values; ++i) {
				FieldId fieldId = contextProvider.getObjectManager().readFieldId(bytes);
				FieldInfo fieldInfo = fieldId.get();
				ClassInfo fieldClassInfo = fieldInfo.getTypeClassInfo();
				Tag tag = Tag.classInfoToTag(fieldClassInfo);
				Value valueUntagged = tag.readValue(bytes);

				valueUntagged.modify(obj.getFields(), fieldInfo.getFieldIndex());

			}

		}
	},

	/**
	 * Returns monitor information for an object. All threads int the VM must be
	 * suspended.Requires {@link CapabilitiesNew#CAN_GET_MONITOR_INFO}
	 * capability.
	 */
	MONITORINFO(5) {
		@Override
		public void execute(ObjectId objectId, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ElementInfo elementInfo = objectId.get();
			Monitor monitor = elementInfo.getMonitor();
			
			// The monitor owner, or null if it is not currently owned.  
			ThreadInfo ownerThreadInfo = monitor.getLockingThread();
			if (ownerThreadInfo == null) {
				// is not currently owned
				NullObjectId.instantWrite(os);
			} else {
				ThreadId ownerThreadId = contextProvider.getObjectManager().getThreadId(ownerThreadInfo);
				ownerThreadId.write(os);
			}
			
			// The number of times the monitor has been entered.  
			int entryCount = monitor.getLockCount();
			os.writeInt(entryCount);
			
			// The number of threads that are waiting for the monitor 0 if there is no current owner  
			int waiters = monitor.getNumberOfWaitingThreads();
			os.writeInt(waiters);
			
			for (ThreadInfo waitingThread : monitor.getWaitingThreads()) {
				// A thread waiting for this monitor.  
				ThreadId threadId = contextProvider.getObjectManager().getThreadId(waitingThread);
				threadId.write(os);
			}
		}
	},
	INVOKEMETHOD(6) {
		@Override
		public void execute(ObjectId objectId, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ThreadId threadId = contextProvider.getObjectManager().readThreadId(bytes);
			ReferenceTypeId clazz = contextProvider.getObjectManager().readReferenceTypeId(bytes);
			MethodInfo methodInfo = VirtualMachineHelper.getClassMethod(clazz.get(), bytes.getLong());
			int arguments = bytes.getInt();
			Value[] values = new Value[arguments];
			for (int i = 0; i < arguments; ++i) {
				values[i] = Tag.bytesToValue(bytes);
			}
			int options = bytes.getInt();

			MethodResult methodResult = VirtualMachineHelper.invokeMethod(objectId.get(), methodInfo, values, threadId.getInfoObject(), options);
			methodResult.write(os);
		}

	},
	DISABLECOLLECTION(7) {
		@Override
		public void execute(ObjectId objectId, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			contextProvider.getVirtualMachine().disableCollection(objectId);
		}
	},
	ENABLECOLLECTION(8) {
		@Override
		public void execute(ObjectId objectId, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			contextProvider.getVirtualMachine().enableCollection(objectId);
		}
	},
	ISCOLLECTED(9) {
		@Override
		public void execute(ObjectId objectId, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			os.writeBoolean(objectId.isNull());
		}
	};
	private byte commandId;

	private ObjectReferenceCommand(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, ObjectReferenceCommand> map = new ReverseEnumMap<Byte, ObjectReferenceCommand>(ObjectReferenceCommand.class);

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public ObjectReferenceCommand convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
		ObjectId objectId = contextProvider.getObjectManager().readObjectId(bytes);
		execute(objectId, bytes, os, contextProvider);
	}

	public abstract void execute(ObjectId objectId, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
			JdwpError;

}