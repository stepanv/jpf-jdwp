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

import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Invalid location.
 * </p>
 * 
 * @author stepan
 * 
 */
public class InvalidLocationException extends JdwpException {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1139986632295562140L;

  /**
   * Constructs the {@link InvalidLocationException} with the error message.
   * 
   * @param message
   *          The message to report.
   * @param cause
   *          The cause or null.
   */
  public InvalidLocationException(String message, Throwable cause) {
    super(ErrorType.INVALID_LOCATION, message, cause);
  }

  /**
   * Constructs the {@link InvalidLocationException} with the error message
   * assembled from the given parameters.
   * 
   * @param classInfo
   *          The class to report or null.
   * @param methodInfo
   *          The method to report or null.
   * @param instruction
   *          The instruction to report or null.
   * @param index
   *          The index to report or null.
   */
  public InvalidLocationException(ClassInfo classInfo, MethodInfo methodInfo, Instruction instruction, Long index) {
    this(message(classInfo, methodInfo, instruction, index), null);
  }

  /**
   * Assembles the error message.
   * 
   * @param classInfo
   *          The class to report or null.
   * @param methodInfo
   *          The method to report or null.
   * @param instruction
   *          The instruction to report or null.
   * @param index
   *          The index to report or null.
   * @return The error message.
   */
  private static String message(ClassInfo classInfo, MethodInfo methodInfo, Instruction instruction, Long index) {
    StringBuilder sb = new StringBuilder();
    if (classInfo != null) {
      sb.append(" [Class: '").append(classInfo).append("']");
    }
    if (methodInfo != null) {
      sb.append(" [Method: '").append(methodInfo).append("']");
    }
    if (instruction != null) {
      sb.append(" [Instruction: '").append(instruction).append("']");
    }
    if (index != null) {
      sb.append(" [Index: '").append(index).append("']");
    }
    return sb.toString();
  }

}
