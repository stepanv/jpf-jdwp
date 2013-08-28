package gov.nasa.jpf.jdwp.exception;

import gov.nasa.jpf.jdwp.id.MethodId;

public class InvalidMethodId extends InvalidIdentifier {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1467080209913560977L;

  public InvalidMethodId(MethodId methodId) {
    super(ErrorType.INVALID_METHODID, methodId);
  }

}
