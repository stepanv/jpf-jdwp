package gov.nasa.jpf.jdwp;

import gov.nasa.jpf.jdwp.exception.InvalidMethodId;
import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.MethodId;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.special.NullObjectId;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.DirectCallStackFrame;
import gov.nasa.jpf.vm.DynamicElementInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ExceptionInfo;
import gov.nasa.jpf.vm.Heap;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Types;
import gov.nasa.jpf.vm.UncaughtException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VirtualMachineHelper {

	/**
	 * Returns the thread's call stack
	 * 
	 * @param thread
	 *            thread for which to get call stack
	 * @param start
	 *            index of first frame to return
	 * @param length
	 *            number of frames to return (-1 for all frames)
	 * @return a list of frames
	 */
	public static List<StackFrame> getFrames(ThreadInfo thread, int start, int length) {

		List<StackFrame> frames = new ArrayList<StackFrame>();
		Iterator<StackFrame> stackIterator = thread.iterator();

		for (int currentPosition = 0, currentLenght = 0; stackIterator.hasNext();) {
			StackFrame stackFrame = stackIterator.next();
			if (!stackFrame.isSynthetic()) {
				if (start <= currentPosition) {
					if (length == -1 || ++currentLenght <= length) {
						frames.add(stackFrame);
					} else {
						return frames;
					}
				}
				++currentPosition;
			}
		}
		return frames;
	}

	/**
	 * Returns the number of frames in the thread's stack
	 * 
	 * @param thread
	 *            the thread for which to get a frame count
	 * @return the number of frames in the thread's stack
	 */
	public static int getFrameCount(ThreadInfo thread) {
		int frameCount = 0;
		for (Iterator<StackFrame> stackIterator = thread.iterator(); stackIterator.hasNext();) {
			StackFrame stackFrame = stackIterator.next();
			if (!stackFrame.isSynthetic()) {
				++frameCount;
			}
		}
		return frameCount;
	}

	public static MethodInfo getClassMethod(ClassInfo clazz, long id) throws JdwpError {
		System.out.println("looking for METHOD global id: " + id + " of CLASS: " + clazz);
		for (MethodInfo methodInfo : clazz.getDeclaredMethodInfos()) {
			if (id == methodInfo.getGlobalId()) {
				System.out.println("METHOD found: " + methodInfo);
				return methodInfo;
			}
		}
		// also try super types
		if (clazz.getSuperClass() != null) {
			return getClassMethod(clazz.getSuperClass(), id);
		}
		throw new InvalidMethodId(new MethodId(id));
	}

	public static class MethodResult {
		private ObjectId exception;
		private Value value;

		public MethodResult(Value value, ObjectId exception) {
			// TODO use Null object pattern everywhere ...
			this.value = value != null ? value : NullObjectId.getInstance();
			this.exception = exception != null ? exception : NullObjectId.getInstance();
		}

		public void write(DataOutputStream os) throws IOException {
			value.writeTagged(os);
			exception.writeTagged(os);
		}

	}

	public static MethodResult invokeMethod(DynamicElementInfo object, MethodInfo method, Value[] values, ThreadInfo thread, int options) throws InvalidObject {
		return invokeMethod(object, method, values, thread, options, false);
	}

	public static MethodResult invokeConstructor(MethodInfo method, Value[] values, ThreadInfo thread, int options) throws InvalidObject {
		return invokeMethod(null, method, values, thread, options, true);
	}

	private static MethodResult invokeMethod(DynamicElementInfo object, MethodInfo method, Value[] values, ThreadInfo thread, int options, boolean isConstructor)
			throws InvalidObject {
		// TODO (maybe this is not a TODO) we're supposed to resume all threads
		// unless INVOKE_SINGLE_THREADED was specified - to prevent deadlocks
		// we're not able to achieve this with JPF since JPF would inspect all
		// states and report where the deadlock occurs

		// TODO [for PJA] What is the best way to execute a method
		// it's typical, we want to execute obj.toString() when generating a
		// popup of a hover info when inspecting an object
		System.out.println("Executing method: " + method + " of object instance: " + object);

		DirectCallStackFrame frame = method.createDirectCallStackFrame(thread, values.length);
		frame.setFireWall();

		ElementInfo constructedElementInfo = null;

		if (isConstructor) {

			Heap heap = thread.getHeap();
			ClassInfo ci = method.getClassInfo();

			if (!ci.isRegistered()) {
				ci.registerClass(thread);
			}

			// since this is a NEW, we also have to pushClinit
			if (!ci.isInitialized()) {
				if (ci.initializeClass(thread)) {
					throw new RuntimeException("HAS TO BE IMPLEMENTED"); // TODO
					// TODO return thread.getPC(); // reexecute this instruction
					// once we return from the clinits
				}
			}

			if (heap.isOutOfMemory()) { // simulate OutOfMemoryError
				throw new RuntimeException("HAS TO BE IMPLEMENTED"); // TODO
				// TODO return
				// ti.createAndThrowException("java.lang.OutOfMemoryError",
				// "trying to allocate new " + cname);
			}

			constructedElementInfo = heap.newObject(ci, thread);
			int objRef = constructedElementInfo.getObjectRef();

			// pushes the object stub onto the stack so that it can be filled by
			// the constructor
			frame.pushRef(objRef);
		}

		// push this on a stack
		if (object != null) { // when obj == null then method is static (and we
								// don't need to push this on a stack)
			frame.pushRef(object.getObjectRef());
		}

		for (Value value : values) {
			System.out.println(value);
			
			// TODO we should probably call rather frame.setArgument() as in JPF_gov_nasa_jpf_test_basic_MJITest#nativeHiddenRoundtrip__I__I
			value.push(frame);
		}

		try {
			thread.executeMethodHidden(frame);
			// ti.advancePC();

		} catch (UncaughtException ux) { // frame's method is firewalled
			System.out.println("# hidden method execution failed, leaving nativeHiddenRoundtrip: " + ux);
			ExceptionInfo exceptionInfo = thread.getPendingException();
			thread.clearPendingException();
			ObjectId exception = JdwpObjectManager.getInstance().getObjectId(exceptionInfo.getException());
			thread.popFrame(); // this is still the DirectCallStackFrame, and we
								// want to continue execution
			return new MethodResult(null, exception);
		}

		Value returnValue;
		if (isConstructor) {
			returnValue = JdwpObjectManager.getInstance().getObjectId(constructedElementInfo);
		} else {
			ClassInfo returnedClassInfo = ClassInfo.getInitializedClassInfo(Types.getClassNameFromTypeName(method.getReturnTypeName()), thread);
			returnValue = Tag.classInfoToTag(returnedClassInfo).peekValue(frame);
		}

		System.out.println("# exit nativeHiddenRoundtrip: " + returnValue);

		return new MethodResult(returnValue, null);

	}

}
