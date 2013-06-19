package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.jdwp.value.Value;
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
 * @since JDWP version 1.6
 * @author stepan
 * 
 */
public class MethodExitWithReturnValueEvent extends LocatableEvent implements Locatable {

	private Value value;

	/**
	 * 
	 * @param threadInfo
	 *            Thread which exited method
	 * @param location
	 *            Location of exit
	 * @param value
	 *            Value that will be returned by the method
	 */
	public MethodExitWithReturnValueEvent(ThreadInfo threadInfo, Location location, Value value) {
		super(EventKind.METHOD_EXIT_WITH_RETURN_VALUE, threadInfo, location);
		this.value = value;
	}

	@Override
	protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
		value.write(os);
	}

}
