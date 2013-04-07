package gov.nasa.jpf.jdwp.event;

import java.io.DataOutputStream;
import java.io.IOException;

import gov.nasa.jpf.jdwp.JdwpObjectManager;
import gov.nasa.jpf.jdwp.event.filter.ExceptionOnlyFilter;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.vm.ClassInfo;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Notification of an exception in the target VM. If the exception is thrown
 * from a non-native method, the exception event is generated at the location
 * where the exception is thrown.<br/>
 * If the exception is thrown from a native method, the exception event is
 * generated at the first non-native location reached after the exception is
 * thrown.
 * </p>
 * 
 * @author stepan
 * 
 */
public class ExceptionEvent extends LocatableEvent implements ExceptionOnlyFilterable, LocationOnlyFilterable {

	private ClassInfo exception;
	private Location catchLocation;

	/**
	 * Creates Exception event.
	 * 
	 * @param threadId
	 *            Thread with exception
	 * @param location
	 *            Location of exception throw (or first non-native location
	 *            after throw if thrown from a native method)
	 * @param exception
	 *            Thrown exception
	 * @param catchLocation
	 *            Location of catch, or 0 if not caught. An exception is
	 *            considered to be caught if, at the point of the throw, the
	 *            current location is dynamically enclosed in a try statement
	 *            that handles the exception. (See the JVM specification for
	 *            details). If there is such a try statement, the catch location
	 *            is the first location in the appropriate catch clause.<br/>
	 * 
	 *            If there are native methods in the call stack at the time of
	 *            the exception, there are important restrictions to note about
	 *            the returned catch location. In such cases, it is not possible
	 *            to predict whether an exception will be handled by some native
	 *            method on the call stack. Thus, it is possible that exceptions
	 *            considered uncaught here will, in fact, be handled by a native
	 *            method and not cause termination of the target VM.
	 *            Furthermore, it cannot be assumed that the catch location
	 *            returned here will ever be reached by the throwing thread. If
	 *            there is a native frame between the current location and the
	 *            catch location, the exception might be handled and cleared in
	 *            that native method instead.<br/>
	 * 
	 *            Note that compilers can generate try-catch blocks in some
	 *            cases where they are not explicit in the source code; for
	 *            example, the code generated for <code>synchronized</code> and
	 *            <code>finally</code> blocks can contain implicit try-catch
	 *            blocks. If such an implicitly generated try-catch is present
	 *            on the call stack at the time of the throw, the exception will
	 *            be considered caught even though it appears to be uncaught
	 *            from examination of the source code.
	 */
	public ExceptionEvent(ThreadId threadId, Location location, ClassInfo exception, Location catchLocation) {
		super(EventKind.EXCEPTION, threadId, location);

		this.exception = exception;
		this.catchLocation = catchLocation;
	}

	@Override
	protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
		JdwpObjectManager.getInstance().getObjectId(exception).writeTagged(os);
		catchLocation.write(os);
	}

	@Override
	public boolean visit(ExceptionOnlyFilter exceptionOnlyFilter) {
		throw new RuntimeException("NOT IMPLEMENTED YET");
	}

}
