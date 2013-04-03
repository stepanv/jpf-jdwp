package gov.nasa.jpf.jdwp.command;

import gnu.classpath.jdwp.VMIdManager;
import gnu.classpath.jdwp.util.JdwpString;
import gnu.classpath.jdwp.util.Signature;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.variable.StringRaw;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.FieldInfo;
import gov.nasa.jpf.jvm.MethodInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Set;

public enum ReferenceTypeCommand implements Command, ConvertibleEnum<Byte, ReferenceTypeCommand> {
	SIGNATURE(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			 ReferenceTypeId refId = contextProvider.getObjectManager().readReferenceTypeId(bytes);
			    String sig = Signature.computeClassSignature(refId.get());
			    JdwpString.writeString(os, sig);
			
		}
	}, 
	CLASSLOADER(2) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			
		}
	}, 
	MODIFIERS(3) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			
		}
	}, 
	FIELDS(4) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			 ReferenceTypeId refId = contextProvider.getObjectManager().readReferenceTypeId(bytes);
			    ClassInfo clazz = refId.get();

			    FieldInfo[] fields = clazz.getInstanceFields();
			    os.writeInt(fields.length);
			    for (int i = 0; i < fields.length; i++)
			      {
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
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ReferenceTypeId refId = contextProvider.getObjectManager().readReferenceTypeId(bytes);
		    ClassInfo clazz = refId.get();

		    System.out.println("METHODS FOR CLASS: " + clazz + " JDWP ID: " + VMIdManager.getDefault().getObjectId(clazz));
		    MethodInfo[] methods = clazz.getDeclaredMethodInfos();
		    os.writeInt (methods.length);
		    for (int i = 0; i < methods.length; i++)
		      {
		        MethodInfo method = methods[i];
		        os.writeLong(method.getGlobalId());
		        System.out.println("METHOD: '" + method.getName() + "', signature: " + method.getSignature() + " (global id: " + method.getGlobalId() + ")");
		        //method.writeId(os);
		        JdwpString.writeString(os, method.getName());
		        JdwpString.writeString(os, method.getSignature());
		        os.writeInt(method.getModifiers());
		      }
			
		}
	}, 
	GETVALUES(6) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			
		}
	}, 
	SOURCEFILE(7) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			
		}
	}, 
	NESTEDTYPES(8) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			
		}
	}, 
	STATUS(9) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			
		}
	}, 
	INTERFACES(10) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ReferenceTypeId refId = contextProvider.getObjectManager().readReferenceTypeId(bytes);
		    ClassInfo clazz = refId.get();
		    Set<ClassInfo> interfaces = clazz.getAllInterfaceClassInfos();
		    os.writeInt(interfaces.size());
		    for (Iterator<ClassInfo> i = interfaces.iterator(); i.hasNext();)
		      {
		        ClassInfo interfaceClass = i.next();
		        ReferenceTypeId intId = contextProvider.getObjectManager().getReferenceTypeId(interfaceClass);
		        intId.write(os);
		      }

			
		}
	}, 
	CLASSOBJECT(11) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			
		}
	}, 
	SOURCEDEBUGEXTENSION(12) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			
		}
	}, 
	SIGNATUREWITHGENERIC(13) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			
		}
	}, 
	FIELDSWITHGENERIC(14) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			
		}
	}, 
	METHODSWITHGENERIC(15) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
			
		}
	};
	
	private byte commandId;

	private ReferenceTypeCommand(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, ReferenceTypeCommand> map = new ReverseEnumMap<Byte, ReferenceTypeCommand>(ReferenceTypeCommand.class);


	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public ReferenceTypeCommand convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	@Override
	public abstract void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;
}
