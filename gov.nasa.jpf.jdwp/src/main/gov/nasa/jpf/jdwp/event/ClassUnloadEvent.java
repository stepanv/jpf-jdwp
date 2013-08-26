package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.event.filter.ClassFilter;
import gov.nasa.jpf.jdwp.value.JdwpString;

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
   *          Type signature
   */
  public ClassUnloadEvent(String signature) {
    super(EventKind.CLASS_UNLOAD);
    this.signature = signature;
  }

  @Override
  protected void writeSpecific(DataOutputStream os) throws IOException {
    JdwpString.write(signature, os);
  }

  @Override
  public boolean matches(ClassFilter classMatchFilter) {
    return classMatchFilter.matches(signature);
  }

}
