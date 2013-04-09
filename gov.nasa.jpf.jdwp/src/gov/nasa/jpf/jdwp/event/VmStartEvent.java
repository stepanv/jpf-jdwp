package gov.nasa.jpf.jdwp.event;

import java.io.DataOutputStream;
import java.io.IOException;

import gov.nasa.jpf.jdwp.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Notification of initialization of a target VM. This event is received before
 * the main thread is started and before any application code has been executed.
 * Before this event occurs a significant amount of system code has executed and
 * a number of system classes have been loaded. This event is always generated
 * by the target VM, even if not explicitly requested.
 * </p>
 * 
 * @author stepan
 * 
 */
public class VmStartEvent extends EventBase implements Threadable {

	/**
	 * Creates VM Start event.
	 * 
	 * @param currentThread
	 *            The initial thread of the VM.
	 */
	public VmStartEvent(ThreadInfo currentThread) {
		super(EventKind.VM_START, (ThreadId) JdwpObjectManager.getInstance().getObjectId(currentThread));
	}

	@Override
	protected void writeSpecific(DataOutputStream os) throws IOException {
	}

}
