package gov.nasa.jpf.jdwp.command;

import gnu.classpath.jdwp.JdwpConstants;
import gnu.classpath.jdwp.VMVirtualMachine;
import gnu.classpath.jdwp.util.JdwpString;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.id.object.ThreadId.ThreadStatus;
import gov.nasa.jpf.jdwp.variable.StringRaw;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum ThreadReferenceCommand implements Command, ConvertibleEnum<Byte, ThreadReferenceCommand> {
	NAME(1) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			ThreadId tid = (ThreadId) contextProvider.getObjectManager().readObjectId(bytes);
		    ThreadInfo thread = tid.get();
		    new StringRaw(thread.getName()).write(os);

		}
	},
	SUSPEND(2) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	RESUME(3) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
		    ThreadId tid = (ThreadId) contextProvider.getObjectManager().readObjectId(bytes);
		    ThreadInfo thread = tid.get();
		    contextProvider.getVirtualMachine().resumeAllThreads(); // TODO solve this

		}
	},
	STATUS(4) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			// TODO not fully implemented
			ThreadId tid = contextProvider.getObjectManager().readThreadId(bytes);
		    ThreadInfo thread = tid.get();
		    ThreadStatus threadStatus = threadStatus(thread);
		
		    // There's only one possible SuspendStatus...
		    int suspendStatus = JdwpConstants.SuspendStatus.SUSPENDED;

		    os.writeInt(threadStatus.identifier());
		    os.writeInt(suspendStatus);

		}
	},
	THREADGROUP(5) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			 ThreadId tid = contextProvider.getObjectManager().readThreadId(bytes);
			    ThreadInfo thread = tid.get();
			    int group = thread.getThreadGroupRef();
			    ElementInfo ei = contextProvider.getVirtualMachine().getJpf().getVM().getHeap().get(group);
			    ObjectId groupId = contextProvider.getObjectManager().getObjectId(ei);
			    groupId.write(os);

		}
	},
	FRAMES(6) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	FRAMECOUNT(7) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	OWNEDMONITORS(8) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	CURRENTCONTENDEDMONITOR(9) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	STOP(10) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	INTERRUPT(11) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	SUSPENDCOUNT(12) {
		@Override
		public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	};
	private byte commandId;

	private ThreadReferenceCommand(int commandId) {
		this.commandId = (byte) commandId;
	}

	private static ReverseEnumMap<Byte, ThreadReferenceCommand> map = new ReverseEnumMap<Byte, ThreadReferenceCommand>(ThreadReferenceCommand.class);

	@Override
	public Byte identifier() {
		return commandId;
	}

	@Override
	public ThreadReferenceCommand convert(Byte val) throws JdwpError {
		return map.get(val);
	}
	
	private static ThreadStatus threadStatus(ThreadInfo thread) {
		// [TODO] not fully implemented yet
		  switch (thread.getState()) {
		  case BLOCKED:
			  return ThreadStatus.WAIT;
		  case RUNNING:
			  return ThreadStatus.RUNNING;
		  case SLEEPING:
			  return ThreadStatus.SLEEPING;
		  default:
			  return ThreadStatus.RUNNING;
		  }
	}

	@Override
	public abstract void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;
}
