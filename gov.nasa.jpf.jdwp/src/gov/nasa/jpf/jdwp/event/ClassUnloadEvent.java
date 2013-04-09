package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.event.filter.ClassFilter;
import gov.nasa.jpf.jdwp.variable.StringRaw;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Notification of a class unload in the target VM.
 * </p>
 * <p>
 * There are severe constraints on the debugger back-end during garbage
 * collection, so unload information is greatly limited.
 * </p>
 * 
 * @author stepan
 * 
 */
public class ClassUnloadEvent extends EventBase implements ClassFilterable {

	private String signature;

	/**
	 * Creates Class Unload event.
	 * 
	 * @param signature
	 *            Type signature
	 */
	public ClassUnloadEvent(String signature) {
		super(EventKind.CLASS_UNLOAD, null);
		this.signature = signature;
	}

	/**
	 * Must be overridden because this event type, as an exception, doesn't have
	 * associated thread
	 */
	@Override
	public void write(DataOutputStream os, int requestId) throws IOException {
		os.writeInt(requestId);
		new StringRaw(signature).write(os);
	}

	@Override
	protected void writeSpecific(DataOutputStream os) throws IOException {
	}

	@Override
	public boolean matches(ClassFilter classMatchFilter) {
		return classMatchFilter.accepts(signature);
	}

}
