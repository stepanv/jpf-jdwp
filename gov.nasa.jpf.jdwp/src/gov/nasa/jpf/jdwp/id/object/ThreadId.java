package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.command.ConvertibleEnum;
import gov.nasa.jpf.jdwp.command.IdentifiableEnum;
import gov.nasa.jpf.jdwp.command.ReverseEnumMap;
import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.ThreadInfo;

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
		this(id, threadInfo.getThreadObject(), threadInfo);
	}
	
	private ThreadId(long id, ElementInfo elementInfo, ThreadInfo threadInfo) {
		super(Tag.THREAD, id, elementInfo, threadInfo);
	}

	public ThreadId(long id, ElementInfo elementInfo) {
		this(id, elementInfo, getThreadInfo(elementInfo));
	}
	
	public ThreadInfo resolveInfoObject() throws InvalidObject {
		return getThreadInfo(get());
	}

	/**
	 * TODO this method may return null .. i.e. solve the case when there is not
	 * related ThreadInfo .. it's also possible it was an exceptional state when
	 * this happened - the code contained compilation error
	 * 
	 * @param elementInfo
	 * @return
	 */
	private static ThreadInfo getThreadInfo(ElementInfo elementInfo) {
		// we can use any thread actually
		ThreadInfo currentThreadInfo = ThreadInfo.getCurrentThread();
		MJIEnv env = currentThreadInfo.getEnv();
		return env.getThreadInfoForObjRef(elementInfo.getObjectRef());
	}

}
