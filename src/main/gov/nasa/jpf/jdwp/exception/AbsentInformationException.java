package gov.nasa.jpf.jdwp.exception;

public class AbsentInformationException extends JdwpError {

  /**
	 * 
	 */
  private static final long serialVersionUID = 4479627949608944900L;

  public AbsentInformationException() {
    super(ErrorType.ABSENT_INFORMATION);
  }

  public AbsentInformationException(String message) {
    super(ErrorType.ABSENT_INFORMATION, message);
  }

  public AbsentInformationException(Throwable cause) {
    super(ErrorType.ABSENT_INFORMATION, cause);
  }

  public AbsentInformationException(String message, Throwable cause) {
    super(ErrorType.ABSENT_INFORMATION, message, cause);
  }

}
