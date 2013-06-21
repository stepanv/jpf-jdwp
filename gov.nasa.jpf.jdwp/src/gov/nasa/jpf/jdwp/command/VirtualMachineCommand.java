package gov.nasa.jpf.jdwp.command;

import gnu.classpath.jdwp.event.EventManager;
import gov.nasa.jpf.jdwp.ClassStatus;
import gov.nasa.jpf.jdwp.JdwpConstants;
import gov.nasa.jpf.jdwp.VirtualMachine.Capabilities;
import gov.nasa.jpf.jdwp.VirtualMachine.CapabilitiesNew;
import gov.nasa.jpf.jdwp.event.EventBase.EventKind;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.TaggableIdentifier;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.value.JdwpString;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

public enum VirtualMachineCommand implements Command, ConvertibleEnum<Byte, VirtualMachineCommand> {

	VERSION(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			JdwpString.write(contextProvider.getJPF().getReporter().getJPFBanner(), os);
			os.writeInt(JdwpConstants.MINOR);
			os.writeInt(JdwpConstants.MAJOR);
			JdwpString.write(System.getProperty("java.version"), os);
			JdwpString.write(System.getProperty("java.vm.name"), os);
		}
	},
	CLASSESBYSIGNATURE(2) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			String signature = JdwpString.read(bytes);

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
			Collection classes = contextProvider.getVirtualMachine().getAllLoadedClasses();
			os.writeInt(classes.size());

			Iterator iter = classes.iterator();
			while (iter.hasNext()) {
				ClassInfo clazz = (ClassInfo) iter.next();
				ReferenceTypeId id = contextProvider.getObjectManager().getReferenceTypeId(clazz);
				id.writeTagged(os);
				JdwpString.write(clazz.getSignature(), os);
				// TODO [for PJA] do we have class statuses in JPF?
				ClassStatus.VERIFIED.write(os);
			}
		}
	},
	ALLTHREADS(4) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ThreadInfo[] threads = VM.getVM().getLiveThreads();
			os.writeInt(threads.length);
			for (ThreadInfo thread : threads) {

				contextProvider.getObjectManager().getThreadId(thread).write(os);
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

			// All event requests are cancelled. 
			for (EventKind eventKind: EventKind.values()) {
				EventManager.getDefault().clearRequests(eventKind);
			}
			
			// All threads suspended by the thread-level resume command or the VM-level resume command are resumed as many times as necessary for them to run. 
			// TODO do the simulation for all threads
			
			// Garbage collection is re-enabled in all cases where it was ObjectReference
			contextProvider.getVirtualMachine().collectAllDisabledObjects();
			
			// TODO now, we need to get prepared for another connection .. this might be a problem with singletons...
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
			// is invoked when inspecting an array field for instance (TODO
			// rewrite this method)
			String string = JdwpString.read(bytes);
			
			// TODO [for PJA] which thread we should use?
			ElementInfo stringElementInfo = VM.getVM().getHeap().newString(string, VM.getVM().getCurrentThread()); 

			ObjectId stringId = contextProvider.getObjectManager().getObjectId(stringElementInfo);

			// Since this string isn't referenced anywhere we'll disable garbage
			// collection on it so it's still around when the debugger gets back
			// to it.
			stringId.disableCollection();
			stringId.write(os);

		}
	},
	CAPABILITIES(12) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			os.writeBoolean(Capabilities.CAN_WATCH_FIELD_MODIFICATION);
			os.writeBoolean(Capabilities.CAN_WATCH_FIELD_ACCESS);
			os.writeBoolean(Capabilities.CAN_GET_BYTECODES);
			os.writeBoolean(Capabilities.CAN_GET_SYNTHETIC_ATTRIBUTE);
			os.writeBoolean(Capabilities.CAN_GET_OWNED_MONITOR_INFO);
			os.writeBoolean(Capabilities.CAN_GET_CURRENT_CONTENDED_MONITOR);
			os.writeBoolean(Capabilities.CAN_GET_MONITOR_INFO);
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
			os.writeBoolean(CapabilitiesNew.CAN_REDEFINE_CLASSES);
			os.writeBoolean(CapabilitiesNew.CAN_ADD_METHOD);
			os.writeBoolean(CapabilitiesNew.CAN_UNRESTRICTEDLY_REDEFINE_CLASSES);
			os.writeBoolean(CapabilitiesNew.CAN_POP_FRAMES);
			os.writeBoolean(CapabilitiesNew.CAN_USE_INSTANCE_FILTERS);
			os.writeBoolean(CapabilitiesNew.CAN_GET_SOURCE_DEBUG_EXTENSION);
			os.writeBoolean(CapabilitiesNew.CAN_REQUEST_V_M_DEATH_EVENT);
			os.writeBoolean(CapabilitiesNew.CAN_SET_DEFAULT_STRATUM);
			os.writeBoolean(CapabilitiesNew.CAN_GET_INSTANCE_INFO);
			os.writeBoolean(CapabilitiesNew.CAN_REQUEST_MONITOR_EVENTS);
			os.writeBoolean(CapabilitiesNew.CAN_GET_MONITOR_FRAME_INFO);
			os.writeBoolean(CapabilitiesNew.CAN_USE_SOURCE_NAME_FILTERS);
			os.writeBoolean(CapabilitiesNew.CAN_GET_CONSTANT_POOL);
			os.writeBoolean(CapabilitiesNew.CAN_FORCE_EARLY_RETURN);

			for (int i = 22; i <= 32; ++i) {
				os.writeBoolean(false);
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
			Collection classes = contextProvider.getVirtualMachine().getAllLoadedClasses();
			os.writeInt(classes.size());

			Iterator iter = classes.iterator();
			while (iter.hasNext()) {
				ClassInfo clazz = (ClassInfo) iter.next();
				ReferenceTypeId id = contextProvider.getObjectManager().getReferenceTypeId(clazz);
				id.writeTagged(os);
				JdwpString.write(clazz.getSignature(), os);
				JdwpString.writeNullAsEmpty(clazz.getGenericSignature(), os);
				// TODO [for PJA] do we have class statuses in JPF?
				ClassStatus.VERIFIED.write(os);
			}

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
