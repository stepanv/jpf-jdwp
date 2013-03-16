package gov.nasa.jpf.jdwp.exception;

public class VmDead extends JdwpError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -37532130944878131L;

	VmDead() {
		super(ErrorType.VM_DEAD);
	}

}
