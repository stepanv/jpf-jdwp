package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Notification of a method invocation in the target VM. This event is generated
 * before any code in the invoked method has executed. Method entry events are
 * generated for both native and non-native methods.
 * </p>
 * <p>
 * In some VMs method entry events can occur for a particular thread before its
 * thread start event occurs if methods are called as part of the thread's
 * initialization.
 * </p>
 * 
 * @author stepan
 * 
 */
public class MethodEntryEvent extends LocatableEvent {

  /**
   * Creates Method Entry event.
   * 
   * @param threadInfo
   *          thread which entered method
   * @param location
   *          The initial executable location in the method.
   */
  public MethodEntryEvent(ThreadInfo threadInfo, Location location) {
    super(EventKind.METHOD_ENTRY, threadInfo, location);
  }

  @Override
  protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
  }

}
