package gov.nasa.jpf.jdwp.exception;

import gov.nasa.jpf.jdwp.command.IdentifiableEnum;

import java.io.DataOutputStream;
import java.io.IOException;

public class JdwpError extends Exception {

	public static enum ErrorType implements IdentifiableEnum<Short, ErrorType> {
		/** No error has occurred. */
		NONE(0),
		/** Passed thread is null, is not a valid thread or has exited. */
		INVALID_THREAD(10),
		/** Thread group invalid. */
		INVALID_THREAD_GROUP(11),
		/** Invalid priority. */
		INVALID_PRIORITY(12),
		/** If the specified thread has not been suspended by an event. */
		THREAD_NOT_SUSPENDED(13),
		/** Thread already suspended. */
		THREAD_SUSPENDED(14),
		/** If this reference type has been unloaded and garbage collected. */
		INVALID_OBJECT(20),
		/** Invalid class. */
		INVALID_CLASS(21),
		/** Class has been loaded but not yet prepared. */
		CLASS_NOT_PREPARED(22),
		/** Invalid method. */
		INVALID_METHODID(23),
		/** Invalid location. */
		INVALID_LOCATION(24),
		/** Invalid field. */
		INVALID_FIELDID(25),
		/** Invalid jframeID. */
		INVALID_FRAMEID(30),
		/** There are no more Java or JNI frames on the call stack. */
		NO_MORE_FRAMES(31),
		/** Information about the frame is not available. */
		OPAQUE_FRAME(32),
		/** Operation can only be performed on current frame. */
		NOT_CURRENT_FRAME(33),
		/** The variable is not an appropriate type for the function used. */
		TYPE_MISMATCH(34),
		/** Invalid slot. */
		INVALID_SLOT(35),
		/** Item already set. */
		DUPLICATE(40),
		/** Desired element not found. */
		NOT_FOUND(41),
		/** Invalid monitor. */
		INVALID_MONITOR(50),
		/** This thread doesn't own the monitor. */
		NOT_MONITOR_OWNER(51),
		/** The call has been interrupted before completion. */
		INTERRUPT(52),
		/**
		 * The virtual machine attempted to read a class file and determined
		 * that the file is malformed or otherwise cannot be interpreted as a
		 * class file.
		 */
		INVALID_CLASS_FORMAT(60),
		/** A circularity has been detected while initializing a class. */
		CIRCULAR_CLASS_DEFINITION(61),
		/**
		 * The verifier detected that a class file, though well formed,
		 * contained some sort of internal inconsistency or security problem.
		 */
		FAILS_VERIFICATION(62),
		/** Adding methods has not been implemented. */
		ADD_METHOD_NOT_IMPLEMENTED(63),
		/** Schema change has not been implemented. */
		SCHEMA_CHANGE_NOT_IMPLEMENTED(64),
		/** The state of the thread has been modified, and is now inconsistent. */
		INVALID_TYPESTATE(65),
		/**
		 * A direct superclass is different for the new class version, or the
		 * set of directly implemented interfaces is different and
		 * canUnrestrictedlyRedefineClasses is false.
		 */
		HIERARCHY_CHANGE_NOT_IMPLEMENTED(66),
		/**
		 * The new class version does not declare a method declared in the old
		 * class version and canUnrestrictedlyRedefineClasses is false.
		 */
		DELETE_METHOD_NOT_IMPLEMENTED(67),
		/** A class file has a version number not supported by this VM. */
		UNSUPPORTED_VERSION(68),
		/**
		 * The class name defined in the new class file is different from the
		 * name in the old class object.
		 */
		NAMES_DONT_MATCH(69),
		/**
		 * The new class version has different modifiers and and
		 * canUnrestrictedlyRedefineClasses is false.
		 */
		CLASS_MODIFIERS_CHANGE_NOT_IMPLEMENTED(70),
		/**
		 * A method in the new class version has different modifiers than its
		 * counterpart in the old class version and and
		 * canUnrestrictedlyRedefineClasses is false.
		 */
		METHOD_MODIFIERS_CHANGE_NOT_IMPLEMENTED(71),
		/** The functionality is not implemented in this virtual machine. */
		NOT_IMPLEMENTED(99),
		/** Invalid pointer. */
		NULL_POINTER(100),
		/** Desired information is not available. */
		ABSENT_INFORMATION(101),
		/** The specified event type id is not recognized. */
		INVALID_EVENT_TYPE(102),
		/** Illegal argument. */
		ILLEGAL_ARGUMENT(103),
		/**
		 * The function needed to allocate memory and no more memory was
		 * available for allocation.
		 */
		OUT_OF_MEMORY(110),
		/**
		 * Debugging has not been enabled in this virtual machine. JVMDI cannot
		 * be used.
		 */
		ACCESS_DENIED(111),
		/** The virtual machine is not running. */
		VM_DEAD(112),
		/** An unexpected internal error has occurred. */
		INTERNAL(113),
		/**
		 * The thread being used to call this function is not attached to the
		 * virtual machine. Calls must be made from attached threads.
		 */
		UNATTACHED_THREAD(115),
		/** object type id or class tag. */
		INVALID_TAG(500),
		/** Previous invoke not complete. */
		ALREADY_INVOKING(502),
		/** Index is invalid. */
		INVALID_INDEX(503),
		/** The length is invalid. */
		INVALID_LENGTH(504),
		/** The string is invalid. */
		INVALID_STRING(506),
		/** The class loader is invalid. */
		INVALID_CLASS_LOADER(507),
		/** The array is invalid. */
		INVALID_ARRAY(508),
		/** Unable to load the transport. */
		TRANSPORT_LOAD(509),
		/** Unable to initialize the transport. */
		TRANSPORT_INIT(510),
		/**   */
		NATIVE_METHOD(511),
		/** The count is invalid. */
		INVALID_COUNT(512);

		private short id;

		ErrorType(int id) {
			this.id = (short) id;
		}

		public void write(DataOutputStream os) throws IOException {
			os.writeShort(id);
		}

		@Override
		public Short identifier() {
			return id;
		}

		@Override
		public ErrorType convert(Short val) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public JdwpError(ErrorType errorType) {
		this.errorType = errorType;
	}
	
	public ErrorType getErrorType() {
		return errorType;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -671231869361344391L;

	protected ErrorType errorType;

	public void write(DataOutputStream os) throws IOException {
		errorType.write(os);
	}

}
