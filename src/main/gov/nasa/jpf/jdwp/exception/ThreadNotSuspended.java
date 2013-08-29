package gov.nasa.jpf.jdwp.exception;

public class ThreadNotSuspended extends JdwpError {

  /**
	 * 
	 */
  private static final long serialVersionUID = -1500457710057267303L;

  public ThreadNotSuspended() {
    super(ErrorType.THREAD_NOT_SUSPENDED);
  }

  public ThreadNotSuspended(String message) {
    super(ErrorType.THREAD_NOT_SUSPENDED, message);
  }

  public ThreadNotSuspended(Throwable cause) {
    super(ErrorType.THREAD_NOT_SUSPENDED, cause);
  }

  public ThreadNotSuspended(String message, Throwable cause) {
    super(ErrorType.THREAD_NOT_SUSPENDED, message, cause);
  }

}
