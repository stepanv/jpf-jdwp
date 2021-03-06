/* 
   Copyright (C) 2013 Stepan Vavra

This file is part of (Java Debug Wire Protocol) JDWP for 
Java PathFinder (JPF) project.

JDWP for JPF is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

JDWP for JPF is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 
 */

package gov.nasa.jpf.jdwp;

import gov.nasa.jpf.jdwp.command.CommandContextProvider;
import gov.nasa.jpf.jdwp.id.JdwpIdManager;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.special.NullObjectId;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.jdwp.value.ValueUtils;
import gov.nasa.jpf.jvm.bytecode.InstructionFactory;
import gov.nasa.jpf.vm.ArrayFields;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.ClassLoaderList;
import gov.nasa.jpf.vm.DirectCallStackFrame;
import gov.nasa.jpf.vm.DynamicElementInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ExceptionInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Fields;
import gov.nasa.jpf.vm.Heap;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.Statics;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.UncaughtException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The helper class for JPF VM related algorithms, functions and procedures.
 * 
 * @author stepan
 * 
 */
public class VirtualMachineHelper {

  final static Logger logger = LoggerFactory.getLogger(VirtualMachineHelper.class);

  /**
   * Returns the thread's call stack without JPD specific synthetic frames that
   * are not supported by the JPDA specification.
   * 
   * @param thread
   *          thread for which to get call stack
   * @param start
   *          index of first frame to return
   * @param length
   *          number of frames to return (-1 for all frames)
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
   * Returns the thread's frame at the specified depth. The depth doesn't count
   * with the JPF specific synthetic frames that are not supported by the JPDA
   * specification.
   * 
   * @param thread
   *          The thread to get the frame for.
   * @param depth
   *          The depth of the frame to get, starting with 0 for the first frame
   *          that was added to the call stack of the thread.
   * @return The stack frame of the provided thread and the specified depth.
   */
  public static StackFrame getFrame(ThreadInfo thread, int depth) {

    Iterator<StackFrame> stackIterator = thread.iterator();

    for (int currentDepth = 0; stackIterator.hasNext();) {
      StackFrame stackFrame = stackIterator.next();
      if (!stackFrame.isSynthetic()) {
        if (currentDepth++ == depth) {
          return stackFrame;
        }
      }
    }
    return null;
  }

  /**
   * Returns the number of frames in the thread's stack. The JPF specific
   * synthetic frames are not included in the returned count.
   * 
   * @param thread
   *          the thread for which to get a frame count
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

  /**
   * The result of a method call.<br/>
   * It can be either a {@link Value} instance or an exception thrown by the
   * call.
   * 
   * <p>
   * <h2>JDWP Specification</h2>
   * The return value (possibly the void value) is included in the reply packet.
   * If the invoked method throws an exception, the exception object ID is set
   * in the reply packet; otherwise, the exception object ID is null.
   * </p>
   * 
   * @author stepan
   * 
   */
  public static class MethodResult {
    private ObjectId exception;
    private Value value;

    /**
     * The {@link MethodResult} method result constructor.
     * 
     * @param value
     *          The value of the method result or a {@link NullObjectId}.
     * @param exception
     *          The exception thrown from the method or a {@link NullObjectId}.
     */
    public MethodResult(Value value, ObjectId exception) {
      this.value = value;
      this.exception = exception;
    }

    /**
     * Writes the method result into the output stream.
     * 
     * @param os
     *          The output stream where to write the method result.
     * @throws IOException
     *           If an I/O error occurs.
     */
    public void write(DataOutputStream os) throws IOException {
      value.writeTagged(os);
      exception.writeTagged(os);
    }

  }

  /**
   * Invoke the method on the given object instance using given values as
   * parameters in a context of the given thread with provided options.
   * 
   * @param object
   *          The object instance to invoke the method on or <code>null</code>
   *          for statics
   * @param method
   *          The method to invoke
   * @param values
   *          The parameters of the method
   * @param thread
   *          The thread which executes the method
   * @param options
   *          JVM specific options
   * @return Instance of {@link MethodResult} where the exception or the result
   *         is stored.
   */
  public static MethodResult invokeMethod(DynamicElementInfo object, MethodInfo method, Value[] values, ThreadInfo thread, int options) {
    return invokeMethod(object, method, values, thread, options, false);
  }

  /**
   * Invoke the constructor provided as a method using given values as
   * parameters in a context of the given thread with provided options.
   * 
   * @param method
   *          The constructor to invoke. No checks whether provided method
   *          really is a constructor are run.
   * @param values
   *          The parameters of the method
   * @param thread
   *          The thread which executes the method
   * @param options
   *          JVM specific options
   * @return Instance of {@link MethodResult} where the exception or the result
   *         is stored.
   */
  public static MethodResult invokeConstructor(MethodInfo method, Value[] values, ThreadInfo thread, int options) {
    return invokeMethod(null, method, values, thread, options, true);
  }

  private static MethodResult invokeMethod(DynamicElementInfo object, MethodInfo method, Value[] values, ThreadInfo thread, int options,
                                           boolean isConstructor) {
    // we're supposed to resume all threads
    // unless INVOKE_SINGLE_THREADED was specified - to prevent deadlocks
    // we're not able to achieve this with JPF since JPF would inspect all
    // states and report where the deadlock occurs

    logger.info("Executding method '{}' in thread '{}' of object instance '{}'", method, thread, object);

    boolean noTopFrame = false;
    if (thread.getTopFrame() == null) {
      // when we exit the direct frame the thread will be TERMINATED which is
      // definitively something we don't want
      noTopFrame = true;

      MethodInfo blockingMethod = new MethodInfo(method, 0, 0);
      blockingMethod.setCode(new Instruction[] { InstructionFactory.getFactory().nop() });

      DirectCallStackFrame blockingFrame = blockingMethod.createRunStartStackFrame(thread);
      blockingFrame.setFireWall();

      thread.setTopFrame(blockingFrame);
    }

    DirectCallStackFrame frame = method.createDirectCallStackFrame(thread, values.length);
    frame.setFireWall();

    ElementInfo constructedElementInfo = null;

    if (isConstructor) {

      Heap heap = thread.getHeap();
      ClassInfo ci = method.getClassInfo();

      if (!ci.isRegistered()) {
        ci.registerClass(thread);
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
      logger.trace("Value: {}", value);
      value.push(frame);
    }

    try {
      thread.executeMethodHidden(frame);
      // ti.advancePC();

    } catch (UncaughtException ux) { // frame's method is firewalled
      logger.debug("# hidden method execution failed, leaving nativeHiddenRoundtrip: ", ux);
      ExceptionInfo exceptionInfo = thread.getPendingException();
      thread.clearPendingException();
      ObjectId exception = JdwpIdManager.getInstance().getObjectId(exceptionInfo.getException());
      thread.popFrame(); // this is still the DirectCallStackFrame, and we
      // want to continue execution
      return new MethodResult(NullObjectId.getInstance(), exception);
    } finally {
      if (noTopFrame) {
        thread.popDirectCallFrame();
      }
    }

    Value returnValue;
    if (isConstructor) {
      returnValue = JdwpIdManager.getInstance().getObjectId(constructedElementInfo);
    } else {
      returnValue = ValueUtils.methodReturnValue(method, frame);
    }

    logger.info("# exit nativeHiddenRoundtrip; returned: {}", returnValue);

    return new MethodResult(returnValue, NullObjectId.getInstance());

  }

  /**
   * Gets referring objects for the given object reference.
   * 
   * @param searchedObjRef
   *          The reference of the object instance that is searched for.
   * @param maxReferrers
   *          The maximum number of referees that is to be returned or zero for
   *          all of them.
   * @param contextProvider
   *          The context provider instance.
   * @return The set of all objectId that has a reference to the given object
   *         reference.
   */
  public static Set<ObjectId> getReferringObjects(int searchedObjRef, int maxReferrers, CommandContextProvider contextProvider) {
    Set<ObjectId> referringObjectRefs = new HashSet<ObjectId>();

    fetchHeapReferences(searchedObjRef, maxReferrers, contextProvider, referringObjectRefs);
    fetchStaticReferences(searchedObjRef, maxReferrers, contextProvider, referringObjectRefs);
    return referringObjectRefs;
  }

  /**
   * Fetches class objects that has given object reference as a reference in
   * their static fields.
   * 
   * @param searchedObjRef
   *          The object reference to search for.
   * @param maxReferrers
   *          The maximum number of referees that is to be returned or zero for
   *          all of them.
   * @param contextProvider
   *          The context provider instance.
   * @param referringObjectRefs
   *          A set of objects that reference the given object.
   */
  private static void fetchStaticReferences(int searchedObjRef, int maxReferrers, CommandContextProvider contextProvider,
                                            Set<ObjectId> referringObjectRefs) {

    ClassLoaderList classLoaderList = contextProvider.getVM().getClassLoaderList();
    for (ClassLoaderInfo classLoaderInfo : classLoaderList) {
      Statics statics = classLoaderInfo.getStatics();

      // iterate over all classes and their respective static areas
      STATIC_EI: for (ElementInfo staticElementInfo : statics) {

        // according to the spec we should return maximum number of
        // referring objects if maxReferrers is greater than 0
        if (hasEnoughReferences(maxReferrers, referringObjectRefs)) {
          return;
        }

        logger.trace("Looking at {} static element info", staticElementInfo);

        // iterate over all static fields
        for (int i = 0; i < staticElementInfo.getNumberOfFields(); ++i) {
          FieldInfo fi = staticElementInfo.getFieldInfo(i);
          if (!fi.isReference()) {
            // the field info does not represent a reference
            continue;
          }

          int objref = staticElementInfo.getReferenceField(fi);

          if (objref == searchedObjRef) {
            // the searched object was found

            ElementInfo classObjectElementInfo = staticElementInfo.getClassInfo().getClassObject();
            ObjectId referringObjectId = contextProvider.getObjectManager().getObjectId(classObjectElementInfo);
            referringObjectRefs.add(referringObjectId);

            logger.debug("Found referring object: {}", classObjectElementInfo);

            continue STATIC_EI;
          }

        }
      }
    }

  }

  /**
   * Fetches objects from the heap that references the given object reference.
   * 
   * @param searchedObjRef
   *          The object reference to search for.
   * @param maxReferrers
   *          The maximum number of referees that is to be returned or zero for
   *          all of them.
   * @param contextProvider
   *          The context provider instance.
   * @param referringObjectRefs
   *          A set of objects that reference the given object.
   */
  private static void fetchHeapReferences(int searchedObjRef, int maxReferrers, CommandContextProvider contextProvider,
                                          Set<ObjectId> referringObjectRefs) {
    EI_LOOP: for (ElementInfo elementInfo : contextProvider.getVM().getHeap()) {
      int i, n;
      Fields fields = elementInfo.getFields();

      // according to the spec we should return maximum number of
      // referring objects if maxReferrers is greater than 0
      if (hasEnoughReferences(maxReferrers, referringObjectRefs)) {
        break;
      }

      if (elementInfo.isArray()) {
        if (fields.isReferenceArray()) {
          n = ((ArrayFields) fields).arrayLength();
          for (i = 0; i < n; i++) {
            int objref = fields.getReferenceValue(i);
            if (objref == searchedObjRef) {

              ObjectId referringObjectId = contextProvider.getObjectManager().getObjectId(elementInfo);
              referringObjectRefs.add(referringObjectId);

              logger.debug("Found referring object: {}", elementInfo);

              // break the inner loop because current
              // elementInfo is already added
              continue EI_LOOP;
            }
          }
        }

      } else { // not an array
        ClassInfo ci = elementInfo.getClassInfo();

        do {
          n = ci.getNumberOfDeclaredInstanceFields();

          for (i = 0; i < n; i++) {
            FieldInfo fi = ci.getDeclaredInstanceField(i);
            if (fi.isReference()) {

              int objref = fields.getReferenceValue(fi.getStorageOffset());
              if (objref == searchedObjRef) {

                ObjectId referringObjectId = contextProvider.getObjectManager().getObjectId(elementInfo);
                referringObjectRefs.add(referringObjectId);

                logger.debug("Found referring object: {}", elementInfo);

                // break the inner loop because current
                // elementInfo is already added
                continue EI_LOOP;
              }
            }
          }
          ci = ci.getSuperClass();
        } while (ci != null);
      }
    }
  }

  /**
   * @param maxReferrers
   * @param referringObjectRefs
   * @return
   */
  private static boolean hasEnoughReferences(int maxReferrers, Set<ObjectId> referringObjectRefs) {
    return maxReferrers > 0 && maxReferrers == referringObjectRefs.size();
  }

  /**
   * Gets the list of all instances of the given class and its all subtypes.
   * 
   * @param classInfo
   *          Represents the class the instances all searched for.
   * @param maxInstances
   *          The maximum number of instances to be returned or if zero all are
   *          returned.
   * @param contextProvider
   *          The context provider.
   * @return The list of all instances.
   */
  public static List<ObjectId> getInstances(ClassInfo classInfo, int maxInstances, CommandContextProvider contextProvider) {
    List<ObjectId> allInstancesFound = new LinkedList<ObjectId>();

    for (ElementInfo elementInfo : contextProvider.getVM().getHeap()) {
      if (maxInstances > 0 && allInstancesFound.size() == maxInstances) {
        break;
      }

      ClassInfo elementClassInfo = elementInfo.getClassInfo();
      if (elementClassInfo.isInstanceOf(classInfo)) {
        ObjectId elementObjectId = contextProvider.getObjectManager().getObjectId(elementInfo);
        allInstancesFound.add(elementObjectId);
      }
    }

    return allInstancesFound;
  }

  /**
   * Gets the count of all the instances of the given class.
   * 
   * @param classInfo
   *          Represents the class the instances all searched for.
   * @param contextProvider
   *          The context provider.
   * @return The count of all instances.
   */
  public static long getInstancesCount(ClassInfo classInfo, CommandContextProvider contextProvider) {
    long numberOfInstancesFound = 0;

    for (ElementInfo elementInfo : contextProvider.getVM().getHeap()) {
      ClassInfo elementClassInfo = elementInfo.getClassInfo();
      if (elementClassInfo.isInstanceOf(classInfo)) {
        ++numberOfInstancesFound;
      }
    }

    return numberOfInstancesFound;
  }

}
