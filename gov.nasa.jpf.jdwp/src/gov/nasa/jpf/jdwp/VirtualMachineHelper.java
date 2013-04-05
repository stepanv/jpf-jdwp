package gov.nasa.jpf.jdwp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

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

}
