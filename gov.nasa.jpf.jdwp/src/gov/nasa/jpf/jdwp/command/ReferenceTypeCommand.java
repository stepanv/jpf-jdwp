package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.object.ClassObjectId;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.special.NullObjectId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.value.StringRaw;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Fields;
import gov.nasa.jpf.vm.MethodInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

public enum ReferenceTypeCommand implements Command, ConvertibleEnum<Byte, ReferenceTypeCommand> {
	SIGNATURE(1) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			new StringRaw(classInfo.getSignature()).write(os);
		}
	},
	CLASSLOADER(2) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			
			ClassLoaderInfo classLoaderInfo = classInfo.getClassLoaderInfo();
			
			if (classLoaderInfo == null) {
				// TODO do this in a uniform way - object manager should return nullObjectId by itself...
				// system classloader
				NullObjectId.getInstance().write(os);
			} else {
				ObjectId objectId = contextProvider.getObjectManager().getClassLoaderObjectId(classLoaderInfo);
				objectId.write(os);
			}
			// ClassInfo clazz = refId.getType();
			// TODO [for PJA] How does JPF work with classloaders? Seems that
			// java.lang.Class#getClassLoader() returns the classloader of
			// underlying VM
			// JPF doesn't care about classloaders?
			// ObjectId oid = new NullObjectId(); // returning null which stands
			// for system classloader
			// throw new RuntimeException("not implemented");
			// ClassLoader loader = clazz.getcl getClassLoader();
			// ObjectId oid = idMan.getObjectId(loader);
			 // returning null which stands
													// for system classloader
			// TODO [for PJA] how is it with classloaders

		}
	},
	MODIFIERS(3) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			os.writeInt(classInfo.getModifiers());

		}
	},
	FIELDS(4) {
		private void writeFields(FieldInfo[] fields, DataOutputStream os, CommandContextProvider contextProvider) throws IOException {
			for (int i = 0; i < fields.length; i++) {
				FieldInfo field = fields[i];
				FieldId fieldId = contextProvider.getObjectManager().getFieldId(field);
				fieldId.write(os);
				new StringRaw(field.getName()).write(os);
				new StringRaw(field.getSignature()).write(os);
				System.out.println("Field: " + field.getName() + ", signature: " + field.getSignature() + ", fieldId: " + fieldId);
				os.writeInt(field.getModifiers());
			}
		}

		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			System.out.println("Fields for: " + classInfo);
			FieldInfo[] instanceFields = classInfo.getDeclaredInstanceFields();
			FieldInfo[] staticFields = classInfo.getDeclaredStaticFields();
			os.writeInt(instanceFields.length + staticFields.length);

			// TODO Specification says, it is supposed to be in the same order
			// as in the source file
			writeFields(instanceFields, os, contextProvider);
			writeFields(staticFields, os, contextProvider);

		}
	},
	METHODS(5) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {

			System.out.println("METHODS FOR CLASS: " + classInfo);
			MethodInfo[] methods = classInfo.getDeclaredMethodInfos();
			os.writeInt(methods.length);
			for (int i = 0; i < methods.length; i++) {
				MethodInfo method = methods[i];
				os.writeLong(method.getGlobalId());
				System.out.println("METHOD: '" + method.getName() + "', signature: " + method.getSignature() + " (global id: " + method.getGlobalId() + ")");
				// method.writeId(os);
				new StringRaw(method.getName()).write(os);

				new StringRaw(method.getSignature()).write(os);
				os.writeInt(method.getModifiers());
			}

		}
	},

	/**
	 * <p>
	 * <h2>JDWP Specification</h2>
	 * Returns the value of one or more static fields of the reference type.
	 * Each field must be member of the reference type or one of its
	 * superclasses, superinterfaces, or implemented interfaces. Access control
	 * is not enforced; for example, the values of private fields can be
	 * obtained.
	 * </p>
	 */
	GETVALUES(6) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			int fields = bytes.getInt();
			os.writeInt(fields);
			
			for (int i = 0; i < fields; ++i) {
				FieldId fieldId = contextProvider.getObjectManager().readFieldId(bytes);
				FieldInfo fieldInfo = fieldId.get();
				
				Fields fieldss = classInfo.getStaticElementInfo().getFields();
				Object object = fieldInfo.getValueObject(fieldss);
				Value val = Tag.classInfoToTag(fieldInfo.getTypeClassInfo()).value(object);
				val.writeTagged(os);
			}
		}
	},
	SOURCEFILE(7) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			String sourceFileName = classInfo.getSourceFileName();
			new StringRaw(SOURCEFILENAME_FIX_PATTERN.matcher(sourceFileName).replaceFirst("")).write(os);
		}
	},
	NESTEDTYPES(8) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
		}
	},
	STATUS(9) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	INTERFACES(10) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			Set<ClassInfo> interfaces = classInfo.getAllInterfaceClassInfos();
			os.writeInt(interfaces.size());
			for (Iterator<ClassInfo> i = interfaces.iterator(); i.hasNext();) {
				ClassInfo interfaceClass = i.next();
				ReferenceTypeId intId = contextProvider.getObjectManager().getReferenceTypeId(interfaceClass);
				intId.write(os);
			}

		}
	},
	/**
	 * Returns the class object corresponding to this type <br/>
	 * This command is used when inspecting an array in Eclipse.<br/>
	 * It is also used when invoking a method of an object instance.
	 * <p>
	 * For a reverse operation refer too
	 * {@link ClassObjectReferenceCommand#REFLECTEDTYPE}
	 * </p>
	 */
	CLASSOBJECT(11) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {

			ClassObjectId clazzObjectId = contextProvider.getObjectManager().getClassObjectId(classInfo);
			clazzObjectId.write(os);

		}
	},
	SOURCEDEBUGEXTENSION(12) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	SIGNATUREWITHGENERIC(13) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	FIELDSWITHGENERIC(14) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	METHODSWITHGENERIC(15) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	};

	private byte commandId;

	private ReferenceTypeCommand(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, ReferenceTypeCommand> map = new ReverseEnumMap<Byte, ReferenceTypeCommand>(ReferenceTypeCommand.class);
	private static final Pattern SOURCEFILENAME_FIX_PATTERN = Pattern.compile("^.*[/\\\\]");

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public ReferenceTypeCommand convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	protected abstract void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
			JdwpError;

	@Override
	public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
		ReferenceTypeId refId = contextProvider.getObjectManager().readReferenceTypeId(bytes);
		execute(refId.get(), bytes, os, contextProvider);
	}

}
