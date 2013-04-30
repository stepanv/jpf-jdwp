package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.ClassStatus;
import gov.nasa.jpf.jdwp.JdwpConstants;
import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.TaggableIdentifier;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.value.StringRaw;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

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
			ThreadInfo[] threads = VM.getVM().getLiveThreads();
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
			contextProvider.getVirtualMachine().resumeAllThreads();
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
			  // is invoked when inspecting an array field for instance (TODO rewrite this method)
		    String string = StringRaw.readString(bytes); 
		    ElementInfo stringElementInfo = VM.getVM().getHeap().newString(string, VM.getVM().getCurrentThread()); // TODO [for PJA] which thread we should use?
		    
		    ObjectId stringId = contextProvider.getObjectManager().getObjectId(stringElementInfo);

		    // Since this string isn't referenced anywhere we'll disable garbage
		    // collection on it so it's still around when the debugger gets back to it.
		    stringId.disableCollection();
		    stringId.write(os);

		}
	},
	CAPABILITIES(12) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			for (int i = 0; i < 7; ++i) {
				os.writeBoolean(false); // we're the most stupid vm ever TODO
			}
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
			CAPABILITIES.execute(bytes, os, contextProvider);
			
			for (int i = 7; i < 32; ++i) {
				os.writeBoolean(false); // TODO check which capabilities we're able to provide
			}
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
