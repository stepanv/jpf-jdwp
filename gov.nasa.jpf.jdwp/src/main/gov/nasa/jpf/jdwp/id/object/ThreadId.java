package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.command.ConvertibleEnum;
import gov.nasa.jpf.jdwp.command.IdentifiableEnum;
import gov.nasa.jpf.jdwp.command.ReverseEnumMap;
import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.exception.InvalidThreadException;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadInfo.State;

/**
 * This class implements the corresponding <code>threadID</code> common data
 * type from the JDWP Specification.
 * 
 * <p>
 * It's important to understand that even if a thread is represented by
 * {@link ThreadInfo} in the JPF, the user (the debugger respectively) inspects
 * an instance of {@link Thread} and therefore any thread during the debug
 * operations is considered as an instance of {@link Thread} on the first place.
 * </p>
 * 
 * <p>
 * <h2>JDWP Specification</h2>
 * Uniquely identifies an object in the target VM that is known to be a thread.
 * </p>
 * 
 * @author stepan
 * 
 */
public class ThreadId extends InfoObjectId<ThreadInfo> {

	/**
	 * <p>
	 * Constants in {@link ThreadStatus} are derived from JVMTI (Java VM Spec,
	 * respectively). Whereas JVMTI introduces more constants only these are
	 * interested (or supported) by the JDWP specification.<br/>
	 * Moreover, the specification isn't even precise how the JVMTI thread
	 * states (JNI thread states) should be mapped to JDWP thread states.
	 * Therefore it's even more unclear how to map JPF thread states (
	 * {@link State} to {@link ThreadStatus}.
	 * </p>
	 * <p>
	 * JPF thread states mapping is implemented in accordance to Harmony and
	 * OpenJDK implementations.
	 * </p>
	 * <p>
	 * These thread states aren't related to the suspension status set from the
	 * debugger. The suspension status is reflected by {@link SuspendStatus}.
	 * </p>
	 * 
	 * @author stepan
	 * 
	 */
	public static enum ThreadStatus implements ConvertibleEnum<Integer, ThreadStatus> {

		/** Thread is terminated */
		ZOMBIE(0),

		/**
		 * Thread is running (doesn't mean it's instructions are executed - the
		 * thread can be waiting to be scheduled.)<br/>
		 */
		RUNNING(1),

		/** Thread is in {@link Thread#sleep(long)} and other alternate methods. */
		SLEEPING(2),

		/** Thread is blocked waiting for a monitor. */
		MONITOR(3),

		/** Thread is waiting for other reasons ... */
		WAIT(4);

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

	/**
	 * Always resolves the info object because {@link ThreadInfo} instances do
	 * change during the SuT execution.
	 * 
	 * @return Resolved Thread Info instance.
	 */
	@Override
	public ThreadInfo getInfoObject() throws InvalidObject {
		return resolveInfoObject();
	}

	public ThreadInfo resolveInfoObject() throws InvalidObject {
		ElementInfo threadElementInfo = get();
		if (threadElementInfo == null) {
			throw new InvalidThreadException(this);
		}
		ThreadInfo threadInfo = getThreadInfo(threadElementInfo);
		if (threadInfo == null) {
			throw new InvalidThreadException(this);
		}
		return threadInfo;
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
