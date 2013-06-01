package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.command.ConvertibleEnum;
import gov.nasa.jpf.jdwp.command.IdentifiableEnum;
import gov.nasa.jpf.jdwp.command.ReverseEnumMap;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class ThreadId extends InfoObjectId<ThreadInfo> {

	public static enum ThreadStatus implements ConvertibleEnum<Integer, ThreadStatus> {
		ZOMBIE(0), RUNNING(1), SLEEPING(2), MONITOR(3), WAIT(4);

		private int id;

		private ThreadStatus(int id) {
			this.id = id;
		}

		@Override
		public Integer identifier() {
			return id;
		}

		private static ReverseEnumMap<Integer, ThreadStatus> map = new ReverseEnumMap<Integer, ThreadId.ThreadStatus>(ThreadStatus.class);

		@Override
		public ThreadStatus convert(Integer val) throws JdwpError {
			return map.get(val);
		}
		
		public static ThreadStatus read(int val) throws JdwpError {
			return ZOMBIE.convert(val);
		}
	}
	
	public enum SuspendStatus implements IdentifiableEnum<Integer> {
		SUSPEND_STATUS_SUSPENDED(1);
		
		private int suspendStatusId;
		
		private SuspendStatus(int suspendStatusId) {
			this.suspendStatusId = suspendStatusId;
		}

		@Override
		public Integer identifier() {
			return suspendStatusId;
		}
	}
	
	public ThreadId(long id, ThreadInfo threadInfo) {
		super(Tag.THREAD, id, threadInfo.getThreadObject(), threadInfo);
	}

	public ThreadId(long id, ElementInfo elementInfo) {
		this(id, getThreadInfo(elementInfo));
	}
	
	private static ThreadInfo getThreadInfo(ElementInfo elementInfo) {
		//int typeNameRef = elementInfo.getReferenceField("name");
		return ThreadInfo.getCurrentThread();
	    //ElementInfo typeName = VM.getVM().getHeap().get(typeNameRef);
	   // String reflectedTypeString = typeName.asString();
	   // ClassInfo ci = ClassInfo.getInitializedClassInfo(reflectedTypeString, VM.getVM().getCurrentThread());
	   // throw new RuntimeException("NOT IMPELEMNTED YET!");
	    // TODO implement ElementInfo to ThreadInfo mapping!
	    //return ci;
	}
	
}
