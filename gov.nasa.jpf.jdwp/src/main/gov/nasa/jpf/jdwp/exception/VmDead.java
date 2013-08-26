package gov.nasa.jpf.jdwp.exception;

public class VmDead extends JdwpError {

  /**
	 * 
	 */
  private static final long serialVersionUID = -37532130944878131L;

  public VmDead(String message, Throwable cause) {
    super(ErrorType.VM_DEAD, message, cause);
  }

  public VmDead(String message) {
    super(ErrorType.VM_DEAD, message);
  }

  public VmDead(Throwable cause) {
    super(ErrorType.VM_DEAD, cause);
  }

  public VmDead() {
    super(ErrorType.VM_DEAD);
  }

}
