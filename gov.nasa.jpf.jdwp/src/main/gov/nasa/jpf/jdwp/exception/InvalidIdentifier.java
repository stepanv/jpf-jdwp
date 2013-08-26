package gov.nasa.jpf.jdwp.exception;

import gov.nasa.jpf.jdwp.id.Identifier;

/**
 * The base exception class for universal representation of all error states
 * related to usage of incorrect identifier.
 * 
 * @author stepan
 * 
 */
public abstract class InvalidIdentifier extends JdwpError {

  /**
	 * 
	 */
  private static final long serialVersionUID = -3101810848944813714L;
  private Identifier<?> identifier;

  protected InvalidIdentifier(ErrorType errorType, Identifier<?> identifier) {
    super(errorType);
    this.identifier = identifier;
  }

  public String toString() {
    return super.toString() + " (Identifier: " + identifier + ")";
  }
}
