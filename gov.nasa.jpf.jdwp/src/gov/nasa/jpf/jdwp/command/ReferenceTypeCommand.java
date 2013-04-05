package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.variable.StringRaw;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.FieldInfo;
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
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	MODIFIERS(3) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	FIELDS(4) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {

			FieldInfo[] fields = classInfo.getInstanceFields();
			os.writeInt(fields.length);
			for (int i = 0; i < fields.length; i++) {
				FieldInfo field = fields[i];
				contextProvider.getObjectManager().getObjectId(field).write(os);
				new StringRaw(field.getName()).write(os);
				new StringRaw(field.getClassInfo().getSignature()).write(os);
				os.writeInt(field.getModifiers());
			}

		}
	},
	METHODS(5) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {

			System.out.println("METHODS FOR CLASS: " + classInfo + " JDWP ID: " + contextProvider.getObjectManager().getObjectId(classInfo));
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
	GETVALUES(6) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

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
	CLASSOBJECT(11) {
		@Override
		protected void execute(ClassInfo classInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			ObjectId clazzObjectId = contextProvider.getObjectManager().getObjectId(classInfo);
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
