package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.FrameId;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.id.object.ThreadId.SuspendStatus;
import gov.nasa.jpf.jdwp.id.object.ThreadId.ThreadStatus;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.jdwp.value.StringRaw;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public enum ThreadReferenceCommand implements Command, ConvertibleEnum<Byte, ThreadReferenceCommand> {
	NAME(1) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			new StringRaw(threadInfo.getName()).write(os);
		}
	},
	SUSPEND(2) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			contextProvider.getVirtualMachine().suspendAllThreads(); // TODO
																		// solve
																		// this
																		// -
																		// just
																		// for a
																		// single
																		// thread
		}
	},
	RESUME(3) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			contextProvider.getVirtualMachine().resumeAllThreads(); // TODO
																	// solve
																	// this -
																	// just for
																	// a single
																	// thread

		}
	},
	STATUS(4) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			// TODO not fully implemented
			ThreadStatus threadStatus = threadStatus(threadInfo);

			os.writeInt(threadStatus.identifier());
			// There's only one possible SuspendStatus...
			os.writeInt(SuspendStatus.SUSPEND_STATUS_SUSPENDED.identifier());

		}
	},
	THREADGROUP(5) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			int group = threadInfo.getThreadGroupRef();
			ElementInfo ei = contextProvider.getVirtualMachine().getJpf().getVM().getHeap().get(group);
			ObjectId groupId = contextProvider.getObjectManager().getObjectId(ei);
			groupId.write(os);
		}
	},

	/**
	 * Returns the current call stack of a suspended thread. The sequence of
	 * frames starts with the currently executing frame, followed by its caller,
	 * and so on. The thread must be suspended, and the returned frameID is
	 * valid only while the thread is suspended.
	 */
	FRAMES(6) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			int startFrame = bytes.getInt();
			int length = bytes.getInt();
			
			List<StackFrame> frames = VirtualMachineHelper.getFrames(threadInfo, startFrame, length);
			os.writeInt(frames.size());
			for (int i = 0; i < frames.size(); i++) {
				StackFrame frame = (StackFrame) frames.get(i);

				// TODO URGENT !! frames cannot be identified by thisobject!!!
				FrameId frameId = contextProvider.getObjectManager().getFrameId(frame);
				frameId.write(os);

				Location location = Location.factorySafe(frame.getPC(), threadInfo);
				location.write(os);
			}

		}
	},
	FRAMECOUNT(7) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			int frameCount = VirtualMachineHelper.getFrameCount(threadInfo);
			os.writeInt(frameCount);

		}
	},
	OWNEDMONITORS(8) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	CURRENTCONTENDEDMONITOR(9) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	STOP(10) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	INTERRUPT(11) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	SUSPENDCOUNT(12) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
				JdwpError {
			os.writeInt(threadInfo.threadDataClone().getSuspendCount());
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

	protected abstract void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException,
			JdwpError;

	@Override
	public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
		ThreadId threadId = contextProvider.getObjectManager().readThreadId(bytes);
		execute(threadId.getInfoObject(), bytes, os, contextProvider);
	}
}
