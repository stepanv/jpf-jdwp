package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.command.VirtualMachineCommand;
import gov.nasa.jpf.jdwp.id.object.ThreadId;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Notification of a completed thread in the target VM. The notification is
 * generated by the dying thread before it terminates. Because of this timing,
 * it is possible for {@link VirtualMachineCommand#ALLTHREADS} to return this
 * thread after this event is received.
 * <p>
 * </p>
 * Note that this event gives no information about the lifetime of the thread
 * object. It may or may not be collected soon depending on what references
 * exist in the target VM. </p>
 * 
 * @author stepan
 * 
 */
public class ThreadDeathEvent extends ThreadableEvent implements Threadable {

	/**
	 * Creates Thread Death event.
	 * 
	 * @param threadId
	 *            Ending thread
	 */
	public ThreadDeathEvent(ThreadId threadId) {
		super(EventKind.THREAD_DEATH, threadId);
	}

	@Override
	protected void writeThreadableSpecific(DataOutputStream os) throws IOException {
	}

}
