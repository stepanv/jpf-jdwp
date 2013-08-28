package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.Event;
import gov.nasa.jpf.jdwp.exception.InvalidCount;

/**
 * <p>
 * Count filter class that restricts reporting of the event to specific moment
 * based on a counter.
 * </p>
 * <p>
 * Can be used with any event, hence {@link Event};
 * </p>
 * <p>
 * <h2>JDWP Specification</h2>
 * Limit the requested event to be reported at most once after a given number of
 * occurrences. The event is not reported the first <code>count - 1</code> times
 * this filter is reached. To request a one-off event, call this method with a
 * count of 1.
 * </p>
 * <p>
 * Once the count reaches 0, any subsequent filters in this request are applied.
 * If none of those filters cause the event to be suppressed, the event is
 * reported. Otherwise, the event is not reported. In either case subsequent
 * events are never reported for this request. This modifier can be used with
 * any event kind.
 * </p>
 * 
 * @author stepan
 * 
 */
public class CountFilter extends Filter<Event> {

  private int count;
  private boolean expired;

  /**
   * Creates Count Filter for the given parameter.
   * 
   * @param count
   *          The event is not reported for <code>count - 1</code> times. Use 1
   *          to report it immediately.
   * @throws InvalidCount
   *           In case the count is invalid (i.e. zero or less).
   */
  public CountFilter(int count) throws InvalidCount {
    super(ModKind.COUNT, Event.class);

    if (count <= 0) {
      throw new InvalidCount(count);
    }

    this.count = count;
    this.expired = false;
  }

  @Override
  public boolean matches(Event event) {
    assert expired == false;

    if (--count > 0) {
      return false;
    }
    expired = true;
    return count == 0;
  }

}
