package gov.nasa.jpf.jdwp.command;

import gnu.classpath.jdwp.VMVirtualMachine;
import gov.nasa.jpf.jdwp.ClassStatus;
import gov.nasa.jpf.jdwp.JdwpConstants;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.TaggableIdentifier;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.variable.StringRaw;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

public enum VirtualMachineCommand implements Command, ConvertibleEnum<Byte, VirtualMachineCommand> {

	VERSION(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			new StringRaw(contextProvider.getJPF().getReporter().getJPFBanner()).write(os);
			os.writeInt(JdwpConstants.MINOR);
			os.writeInt(JdwpConstants.MAJOR);
			new StringRaw(System.getProperty("java.version")).write(os);
			new StringRaw(System.getProperty("java.vm.name")).write(os);
		}
	},
	CLASSESBYSIGNATURE(2) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			String signature = StringRaw.readString(bytes);

			ArrayList<ClassInfo> matchingClasses = new ArrayList<ClassInfo>();

			Collection<ClassInfo> classes = contextProvider.getVirtualMachine().getAllLoadedClasses();

			log.finest("LOOKING FOR CLASS: " + signature);
			for (ClassInfo classInfo : classes) {
				if (signature.equals(classInfo.getSignature())) {
					matchingClasses.add(classInfo);
				}
			}

			os.writeInt(matchingClasses.size());
			for (ClassInfo classInfo : matchingClasses) {
				log.finest("Sending classes by signature: " + classInfo + "");
				ReferenceTypeId id = contextProvider.getObjectManager().getReferenceTypeId(classInfo);
				id.writeTagged(os);

				// TODO [for PJA] do we have class statuses in JPF?
				ClassStatus.VERIFIED.write(os);
			}

		}
	},
	ALLCLASSES(3) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);
		}
	},
	ALLTHREADS(4) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ThreadInfo[] threads = VMVirtualMachine.allThreads();
			  os.writeInt(threads.length);
			  for (ThreadInfo thread : threads) {
				  
				  contextProvider.getObjectManager().getObjectId(thread).write(os);
			  }
			
		}
	},
	TOPLEVELTHREADGROUPS(5) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	DISPOSE(6) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	IDSIZES(7) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			os.writeInt(TaggableIdentifier.SIZE); 
			os.writeInt(TaggableIdentifier.SIZE); 
			os.writeInt(TaggableIdentifier.SIZE); 
			os.writeInt(TaggableIdentifier.SIZE); 
			os.writeInt(TaggableIdentifier.SIZE); 
		}
	},
	SUSPEND(8) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	RESUME(9) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	EXIT(10) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	CREATESTRING(11) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	CAPABILITIES(12) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	CLASSPATHS(13) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	DISPOSEOBJECTS(14) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	HOLDEVENTS(15) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	RELEASEEVENTS(16) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	CAPABILITIESNEW(17) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	REDEFINECLASSES(18) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	SETDEFAULTSTRATUM(19) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	ALLCLASSESWITHGENERIC(20) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	};

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public VirtualMachineCommand convert(Byte val) throws JdwpError {
		return map.get(val);
	}

	private byte commandId;

	private VirtualMachineCommand(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static Logger log = Logger.getLogger(VirtualMachineCommand.class.getName());

	private static ReverseEnumMap<Byte, VirtualMachineCommand> map = new ReverseEnumMap<Byte, VirtualMachineCommand>(VirtualMachineCommand.class);

	public abstract void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;

}
