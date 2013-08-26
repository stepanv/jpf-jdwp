package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.ClassStatus;
import gov.nasa.jpf.jdwp.event.EventRequest.SuspendPolicy;
import gov.nasa.jpf.jdwp.event.filter.ClassFilter;
import gov.nasa.jpf.jdwp.event.filter.ClassOnlyFilter;
import gov.nasa.jpf.jdwp.exception.InvalidIdentifier;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.value.JdwpString;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Notification of a class prepare in the target VM. See the JVM specification
 * for a definition of class preparation. Class prepare events are not generated
 * for primtiive classes (for example, {@link java.lang.Integer#TYPE}).
 * </p>
 * 
 * @author stepan
 * 
 */
public class ClassPrepareEvent extends ThreadableEvent implements Threadable, ClassFilterable, ClassOnlyFilterable,
    SourceNameMatchFilterable {

  private int status;
  private ClassInfo classInfo;

  /**
   * 
   * @param currentThread
   *          Preparing thread. In rare cases, this event may occur in a
   *          debugger system thread within the target VM. Debugger threads take
   *          precautions to prevent these events, but they cannot be avoided
   *          under some conditions, especially for some subclasses of
   *          java.lang.Error. If the event was generated by a debugger system
   *          thread, the value returned by this method is null, and if the
   *          requested {@link SuspendPolicy} for the event was
   *          {@link SuspendPolicy#EVENT_THREAD} all threads will be suspended
   *          instead, and the composite event's suspend policy will reflect
   *          this change. <br/>
   * 
   *          Note that the discussion above does not apply to system threads
   *          created by the target VM during its normal (non-debug) operation.
   * @param classInfo
   *          Type being prepared
   * @param status
   *          Status of type. See {@link ClassStatus}.
   */
  public ClassPrepareEvent(ThreadInfo currentThread, ClassInfo classInfo, int status) {
    super(EventKind.CLASS_PREPARE, currentThread);

    this.classInfo = classInfo;
    this.status = status;

  }

  @Override
  protected void writeThreadableSpecific(DataOutputStream os) throws IOException {
    JdwpObjectManager.getInstance().getReferenceTypeId(classInfo).writeTagged(os);
    JdwpString.write(classInfo.getSignature(), os);
    os.writeInt(status);
  }

  @Override
  public boolean matches(ClassFilter classMatchFilter) {
    return classMatchFilter.matches(classInfo.getName());
  }

  @Override
  public boolean matches(ClassOnlyFilter classOnlyFilter) throws InvalidIdentifier {
    return classOnlyFilter.matches(classInfo);
  }

  @Override
  public String toString() {
    return super.toString() + " for class: " + classInfo;
  }

}
