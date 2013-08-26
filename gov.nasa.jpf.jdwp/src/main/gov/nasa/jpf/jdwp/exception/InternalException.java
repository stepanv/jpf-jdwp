package gov.nasa.jpf.jdwp.exception;

/**
 * This exception stands for any errors that may have happened during the
 * execution of JDWP commands.<br/>
 * Errors of this type are rather bugs than anthing else.
 * 
 * @author stepan
 * 
 */
public class InternalException extends JdwpError {

  /**
	 * 
	 */
  private static final long serialVersionUID = -8092968568063765998L;

  public InternalException(ErrorType errorType) {
    super(ErrorType.INTERNAL);
  }

  public InternalException(String message) {
    super(ErrorType.INTERNAL, message);
  }

  public InternalException(Throwable cause) {
    super(ErrorType.INTERNAL, cause);
  }

  public InternalException(String message, Throwable cause) {
    super(ErrorType.INTERNAL, message, cause);
  }

}
