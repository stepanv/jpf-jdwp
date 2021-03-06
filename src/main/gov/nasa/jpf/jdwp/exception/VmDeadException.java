/* 
   Copyright (C) 2013 Stepan Vavra

This file is part of (Java Debug Wire Protocol) JDWP for 
Java PathFinder (JPF) project.

JDWP for JPF is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

JDWP for JPF is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 
 */

package gov.nasa.jpf.jdwp.exception;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * The virtual machine is not running.
 * </p>
 * 
 * @author stepan
 * 
 */
public class VmDeadException extends JdwpException {

  /**
	 * 
	 */
  private static final long serialVersionUID = -37532130944878131L;

  /**
   * Creates the VM Dead exception.
   * 
   * @param message
   *          The message to be reported.
   * @param cause
   *          The exception cause to be chained.
   */
  public VmDeadException(String message, Throwable cause) {
    super(ErrorType.VM_DEAD, message, cause);
  }

  /**
   * Creates the VM Dead exception.
   * 
   * @param message
   *          The message to be reported.
   */
  public VmDeadException(String message) {
    super(ErrorType.VM_DEAD, message);
  }

  /**
   * Creates the VM Dead exception.
   * 
   * @param cause
   *          The exception cause to be chained.
   */
  public VmDeadException(Throwable cause) {
    super(ErrorType.VM_DEAD, cause);
  }

  /**
   * Creates the VM Dead exception.
   */
  public VmDeadException() {
    super(ErrorType.VM_DEAD);
  }

}
