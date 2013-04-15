package gov.nasa.jpf.jdwp;

import gov.nasa.jpf.jdwp.exception.InvalidMethodId;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VirtualMachineHelper {

	public static StackFrame getFrame(ThreadInfo thread, long frameID) {
		// TODO Auto-generated method stub
		return null;
	}

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
		for (Iterator<StackFrame> stackIterator = thread.iterator(); stackIterator.hasNext();) {
			StackFrame stackFrame = stackIterator.next();
			if (!stackFrame.isSynthetic()) {
				frames.add(stackFrame);
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

	 public static MethodInfo getClassMethod(ClassInfo clazz, long id)
			    throws JdwpError {
				  System.out.println("looking for METHOD global id: " + id + " of CLASS: " + clazz + " JDWP ID: " + JdwpObjectManager.getInstance().getObjectId(clazz));
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
				  throw new InvalidMethodId(id);
			  }
	 
}
