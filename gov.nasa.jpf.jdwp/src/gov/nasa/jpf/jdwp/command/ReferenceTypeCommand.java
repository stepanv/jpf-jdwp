package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.object.ClassObjectId;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.special.NullObjectId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.value.JdwpString;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jdwp.value.Value;
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

	/**
	 * Returns the JNI signature of a reference type. JNI signature formats are
	 * described in the Java Native Inteface Specification <br/>
	 * For primitive classes the returned signature is the signature of the
	 * corresponding primitive type; for example, "I" is returned as the
	 * signature of the class represented by java.lang.Integer.TYPE.
	 */
	SIGNATURE(1) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			executeSignature(classInfo, bytes, os, contextProvider, false);
		}
	},
	CLASSLOADER(2) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {

			ClassLoaderInfo classLoaderInfo = classInfo.getClassLoaderInfo();

			if (classLoaderInfo == null) {
				// TODO do this in a uniform way - object manager should return
				// nullObjectId by itself...
				// system classloader
				NullObjectId.instantWrite(os);
			} else {
				ObjectId objectId = contextProvider.getObjectManager().getClassLoaderObjectId(classLoaderInfo);
				objectId.write(os);
			}
		}
	},
	MODIFIERS(3) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			os.writeInt(classInfo.getModifiers());

		}
	},

	/**
	 * Returns information for each field in a reference type. Inherited fields
	 * are not included. The field list will include any synthetic fields
	 * created by the compiler. Fields are returned in the order they occur in
	 * the class file.
	 */
	FIELDS(4) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			executeFields(classInfo, bytes, os, contextProvider, false);
		}
	},

	/**
	 * Returns information for each method in a reference type. Inherited
	 * methods are not included. The list of methods will include constructors
	 * (identified with the name <tt>&lt;init&gt;</tt>), the initialization
	 * method (identified with the name <tt>&lt;clinit&gt;</tt>) if present, and
	 * any synthetic methods created by the compiler. Methods are returned in
	 * the order they occur in the class file.
	 */
	METHODS(5) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			executeMethods(classInfo, bytes, os, contextProvider, false);
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
			JdwpString.write(SOURCEFILENAME_FIX_PATTERN.matcher(sourceFileName).replaceFirst(""), os);
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

	/**
	 * Returns the JNI signature of a reference type along with the generic
	 * signature if there is one. Generic signatures are described in the
	 * signature attribute section in the Java Virtual Machine Specification,
	 * 3rd Edition.
	 * 
	 * @since JDWP version 1.5
	 */
	SIGNATUREWITHGENERIC(13) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			executeSignature(classInfo, bytes, os, contextProvider, true);
		}
	},

	/**
	 * Returns information, including the generic signature if any, for each
	 * field in a reference type. Inherited fields are not included. The field
	 * list will include any synthetic fields created by the compiler. Fields
	 * are returned in the order they occur in the class file. Generic
	 * signatures are described in the signature attribute section in the Java
	 * Virtual Machine Specification, 3rd Edition.
	 * 
	 * @since JDWP version 1.5
	 */
	FIELDSWITHGENERIC(14) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			executeFields(classInfo, bytes, os, contextProvider, true);
		}
	},

	/**
	 * Returns information, including the generic signature if any, for each
	 * method in a reference type. Inherited methods are not included. The list
	 * of methods will include constructors (identified with the name
	 * <tt>&lt;init&gt;</tt>), the initialization method (identified with the
	 * name <tt>&lt;clinit&gt;</tt>) if present, and any synthetic methods
	 * created by the compiler. Methods are returned in the order they occur in
	 * the class file. Generic signatures are described in the signature
	 * attribute section in the Java Virtual Machine Specification, 3rd Edition.
	 * 
	 * @since JDWP version 1.5
	 */
	METHODSWITHGENERIC(15) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			executeMethods(classInfo, bytes, os, contextProvider, true);
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
		System.out.println("ReferenceType: " + refId.get());
		execute(refId.get(), bytes, os, contextProvider);
	}

	protected void executeSignature(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider, boolean withGeneric)
			throws IOException, JdwpError {
		JdwpString.write(classInfo.getSignature(), os);
		if (withGeneric) {
			JdwpString.writeNullAsEmpty(classInfo.getGenericSignature(), os);
		}
	}

	private void writeFields(FieldInfo[] fields, DataOutputStream os, CommandContextProvider contextProvider, boolean withGeneric) throws IOException {
		for (int i = 0; i < fields.length; i++) {
			FieldInfo field = fields[i];
			FieldId fieldId = contextProvider.getObjectManager().getFieldId(field);
			fieldId.write(os);
			JdwpString.write(field.getName(), os);
			JdwpString.write(field.getSignature(), os);
			if (withGeneric) {
				JdwpString.writeNullAsEmpty(field.getGenericSignature(), os);
			}
			System.out.println("Field: " + field.getName() + ", signature: " + field.getSignature() + ", generic signature: " + field.getGenericSignature()
					+ ", fieldId: " + fieldId);
			os.writeInt(field.getModifiers());
		}
	}

	protected void executeFields(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider, boolean withGeneric)
			throws IOException, JdwpError {
		System.out.println("Fields for: " + classInfo);
		FieldInfo[] instanceFields = classInfo.getDeclaredInstanceFields();
		FieldInfo[] staticFields = classInfo.getDeclaredStaticFields();
		os.writeInt(instanceFields.length + staticFields.length);

		// TODO Specification says, it is supposed to be in the same order
		// as in the source file
		writeFields(instanceFields, os, contextProvider, withGeneric);
		writeFields(staticFields, os, contextProvider, withGeneric);
	}

	protected void executeMethods(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider, boolean withGeneric)
			throws IOException, JdwpError {
		System.out.println("METHODS FOR CLASS: " + classInfo);
		MethodInfo[] methods = classInfo.getDeclaredMethodInfos();
		os.writeInt(methods.length);
		for (int i = 0; i < methods.length; i++) {
			MethodInfo method = methods[i];
			os.writeLong(method.getGlobalId());
			System.out.println("METHOD: '" + method.getName() + "', signature: " + method.getSignature() + ", generic signature: "
					+ method.getGenericSignature() + " (global id: " + method.getGlobalId() + ")");
			// method.writeId(os);
			JdwpString.write(method.getName(), os);

			JdwpString.write(method.getSignature(), os);

			if (withGeneric) {
				JdwpString.writeNullAsEmpty(method.getGenericSignature(), os);
			}

			os.writeInt(method.getModifiers());
		}
	}

}
