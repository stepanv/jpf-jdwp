package gov.nasa.jpf.jdwp.exception;

import gov.nasa.jpf.jdwp.id.object.ObjectId;

public class InvalidObject extends InvalidIdentifier {

  /**
	 * 
	 */
  private static final long serialVersionUID = -3401121682523839373L;

  public InvalidObject(ObjectId objectId) {
    super(ErrorType.INVALID_OBJECT, objectId);
  }

  protected InvalidObject(ErrorType errorType, ObjectId objectId) {
    super(errorType, objectId);
  }
}
