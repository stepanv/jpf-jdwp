package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.type.Location;

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

	public MethodExitEvent(ThreadId threadId, Location location) {
		super(EventKind.METHOD_EXIT, threadId, location);
	}

	@Override
	protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
	}

}
