package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.Threadable;
import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * <p>
 * Can be used with all {@link Threadable} events.
 * </p>
 * <p>
 * <h2>JDWP Specification</h2>
 * Restricts reported events to those in the given thread. This modifier can be
 * used with any event kind except for class unload.
 * </p>
 * 
 * @author stepan
 * 
 */
public class ThreadOnlyFilter extends Filter<Threadable> {

  private ThreadId threadId;

  /**
   * Creates Thread Only filter.
   * 
   * @param threadId
   *          Required thread
   */
  public ThreadOnlyFilter(ThreadId threadId) {
    super(ModKind.THREAD_ONLY, Threadable.class);
    this.threadId = threadId;
  }

  @Override
  public boolean matches(Threadable event) {
    ThreadInfo threadInfo;
    try {
      threadInfo = threadId.getInfoObject();
      return event.getThread() == threadInfo;
    } catch (InvalidObject e) {
      // info object is not accessible and therefore this filter is not
      // effective
      return false;
    }
  }

}
