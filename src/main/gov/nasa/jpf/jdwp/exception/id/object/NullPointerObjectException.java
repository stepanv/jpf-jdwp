/**
 * 
 */
package gov.nasa.jpf.jdwp.exception.id.object;

import gov.nasa.jpf.jdwp.id.object.ObjectId;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Invalid pointer.
 * </p>
 * Null pointer access for {@link ObjectId} instances.
 * 
 * @author stepan
 * 
 */
public class NullPointerObjectException extends InvalidObjectException {

  /**
   * 
   */
  private static final long serialVersionUID = -4471042744599433226L;

  /**
   * Constructs the {@link NullPointerObjectException} exception.
   */
  public NullPointerObjectException() {
    super(ErrorType.NULL_POINTER, null);
  }

}
