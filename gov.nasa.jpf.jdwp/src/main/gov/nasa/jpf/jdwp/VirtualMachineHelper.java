package gov.nasa.jpf.jdwp;

import gov.nasa.jpf.jdwp.command.CommandContextProvider;
import gov.nasa.jpf.jdwp.exception.InvalidMethodId;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.MethodId;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.special.NullObjectId;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.jdwp.value.ValueUtils;
import gov.nasa.jpf.vm.ArrayFields;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.DirectCallStackFrame;
import gov.nasa.jpf.vm.DynamicElementInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ExceptionInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Fields;
import gov.nasa.jpf.vm.Heap;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
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

public class VirtualMachineHelper {

  final static Logger logger = LoggerFactory.getLogger(VirtualMachineHelper.class);

  /**
   * Returns the thread's call stack
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
   * Returns the number of frames in the thread's stack
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

  public static MethodInfo getClassMethod(ClassInfo clazz, long id) throws JdwpError {
    logger.debug("looking for METHOD global id:  of CLASS: {}", id, clazz);
    for (MethodInfo methodInfo : clazz.getDeclaredMethodInfos()) {
      if (id == methodInfo.getGlobalId()) {
        logger.debug("METHOD found: {}", methodInfo);
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
    // TODO (maybe this is not a TODO) we're supposed to resume all threads
    // unless INVOKE_SINGLE_THREADED was specified - to prevent deadlocks
    // we're not able to achieve this with JPF since JPF would inspect all
    // states and report where the deadlock occurs

    // TODO [for PJA] What is the best way to execute a method
    // it's typical, we want to execute obj.toString() when generating a
    // popup of a hover info when inspecting an object
    logger.info("Executing method: " + method + " of object instance: " + object);

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
      logger.trace("Value: {}", value);

      // TODO we should probably call rather frame.setArgument() as in
      // JPF_gov_nasa_jpf_test_basic_MJITest#nativeHiddenRoundtrip__I__I
      value.push(frame);
    }

    try {
      thread.executeMethodHidden(frame);
      // ti.advancePC();

    } catch (UncaughtException ux) { // frame's method is firewalled
      logger.debug("# hidden method execution failed, leaving nativeHiddenRoundtrip: ", ux);
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
      returnValue = ValueUtils.methodReturnValue(method, frame);
    }

    logger.info("# exit nativeHiddenRoundtrip: {}", returnValue);

    return new MethodResult(returnValue, null);

  }

  /**
   * Gets referring objects for the given object reference.
   * 
   * @param searchedObjRef
   *          The reference of the object instance that is searched for.
   * @param maxReferrers
   *          The maximum number of referees that is to be returned.
   * @param contextProvider
   *          The context provider instance.
   * @return The set of all objectId that has a reference to the given object
   *         reference.
   */
  public static Set<ObjectId> getReferringObjects(int searchedObjRef, int maxReferrers, CommandContextProvider contextProvider) {
    Set<ObjectId> referringObjectRefs = new HashSet<ObjectId>();

    EI_LOOP: for (ElementInfo elementInfo : contextProvider.getVM().getHeap()) {
      int i, n;
      Fields fields = elementInfo.getFields();

      // according to the spec we should return maximum number of
      // referring objects if maxReferrers is greater than 0
      if (maxReferrers > 0 && maxReferrers == referringObjectRefs.size()) {
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

    return referringObjectRefs;
  }

  /**
   * Gets the list of all instances of the given class and its all subtypes.
   * 
   * @param classInfo
   *          Represents the class the instances all searched for.
   * @param maxInstances
   *          The maximum number of instances to be returned.
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
