package gov.nasa.jpf.jdwp.command;

import gnu.classpath.jdwp.JdwpConstants;
import gnu.classpath.jdwp.VMVirtualMachine;
import gnu.classpath.jdwp.util.JdwpString;
import gnu.classpath.jdwp.util.Location;
import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.id.object.ThreadId.ThreadStatus;
import gov.nasa.jpf.jdwp.variable.StringRaw;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public enum ThreadReferenceCommand implements Command, ConvertibleEnum<Byte, ThreadReferenceCommand> {
	NAME(1) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
		    new StringRaw(threadInfo.getName()).write(os);
		}
	},
	SUSPEND(2) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			contextProvider.getVirtualMachine().suspendAllThreads(); // TODO solve this - just for a single thread
		}
	},
	RESUME(3) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
		    contextProvider.getVirtualMachine().resumeAllThreads(); // TODO solve this - just for a single thread

		}
	},
	STATUS(4) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			// TODO not fully implemented
		    ThreadStatus threadStatus = threadStatus(threadInfo);
		
		    // There's only one possible SuspendStatus...
		    int suspendStatus = JdwpConstants.SuspendStatus.SUSPENDED;

		    os.writeInt(threadStatus.identifier());
		    os.writeInt(suspendStatus);

		}
	},
	THREADGROUP(5) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			    int group = threadInfo.getThreadGroupRef();
			    ElementInfo ei = contextProvider.getVirtualMachine().getJpf().getVM().getHeap().get(group);
			    ObjectId groupId = contextProvider.getObjectManager().getObjectId(ei);
			    groupId.write(os);
		}
	},
	FRAMES(6) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			int startFrame = bytes.getInt();
		    int length = bytes.getInt();

		    List<StackFrame> frames = VirtualMachineHelper.getFrames(threadInfo, startFrame, length);
		    os.writeInt(frames.size());
		    for (int i = 0; i < frames.size(); i++)
		      {
		    	StackFrame frame = (StackFrame) frames.get(i);
		        os.writeLong(frame.getThis());
		        
		        Instruction instruction = frame.getPC();
				
				while (instruction.getMethodInfo() == null || instruction.getMethodInfo().getClassInfo() == null) {
					instruction = instruction.getNext(threadInfo);
				}
		        Location location = Location.factory(instruction);
		        location.write(os);
		      }

		}
	},
	FRAMECOUNT(7) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			int frameCount = VirtualMachineHelper.getFrameCount(threadInfo);
		    os.writeInt(frameCount);

		}
	},
	OWNEDMONITORS(8) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	CURRENTCONTENDEDMONITOR(9) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	STOP(10) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	INTERRUPT(11) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
			throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

		}
	},
	SUSPENDCOUNT(12) {
		@Override
		protected void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
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

	
	protected abstract void execute(ThreadInfo threadInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError;
	
	@Override
	public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
		ThreadId tid = (ThreadId) contextProvider.getObjectManager().readObjectId(bytes);
	    ThreadInfo thread = tid.get();
	    execute(thread, bytes, os, contextProvider);
	}
}
