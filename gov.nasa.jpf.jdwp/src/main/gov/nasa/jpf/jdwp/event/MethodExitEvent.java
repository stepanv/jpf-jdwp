package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Notification of a method return in the target VM. This event is generated
 * after all code in the method has executed, but the location of this event is
 * the last executed location in the method. Method exit events are generated
 * for both native and non-native methods. Method exit events are not generated
 * if the method terminates with a thrown exception.
 * </p>
 * 
 * @author stepan
 * 
 */
public class MethodExitEvent extends LocatableEvent {

	/**
	 * 
	 * @param threadInfo
	 *            The thread which exited method
	 * @param location
	 *            Location of exit
	 */
	public MethodExitEvent(ThreadInfo threadInfo, Location location) {
		super(EventKind.METHOD_EXIT, threadInfo, location);
	}

	@Override
	protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
	}

}
