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

package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.VirtualMachine.CapabilitiesNew;
import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.exception.IllegalArgumentException;
import gov.nasa.jpf.jdwp.exception.JdwpException;
import gov.nasa.jpf.jdwp.exception.NotImplementedException;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.util.LineTable;
import gov.nasa.jpf.jdwp.util.VariableTable;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.MethodInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * The {@link MethodCommand} enum class implements the {@link CommandSet#METHOD}
 * set of commands. For the detailed specification refer to <a href=
 * "http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#JDWP_Method"
 * >http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#
 * JDWP_Method</a> JDWP 1.6 Specification pages.
 * 
 * @author stepan
 * 
 */
public enum MethodCommand implements Command, ConvertibleEnum<Byte, MethodCommand> {

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns line number information for the method, if present. The line table
   * maps source line numbers to the initial code index of the line. The line
   * table is ordered by code index (from lowest to highest). The line number
   * information is constant unless a new class definition is installed using
   * {@link VirtualMachineCommand#REDEFINECLASSES}.
   * </p>
   */
  LINETABLE(1) {
    @Override
    public void execute(MethodInfo methodInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      LineTable lineTable = new LineTable(methodInfo);
      lineTable.write(os);
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns variable information for the method. The variable table includes
   * arguments and locals declared within the method. For instance methods, the
   * "this" reference is included in the table. Also, synthetic variables may be
   * present.
   * </p>
   */
  VARIABLETABLE(2) {
    @Override
    public void execute(MethodInfo methodInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      VariableTable variableTable = new VariableTable(methodInfo);
      variableTable.write(os);
    }
  },
  
  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Retrieve the method's bytecodes as defined in the JVM Specification.
   * Requires {@link CapabilitiesNew#CAN_GET_BYTECODES} capability.
   * </p>
   */
  BYTECODES(3) {
    @Override
    public void execute(MethodInfo methodInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      // can throw this exception as far as can get bytecodes capability is
      // false.
      throw new NotImplementedException();
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Determine if this method is obsolete. A method is obsolete if it has been
   * replaced by a non-equivalent method using the
   * {@link VirtualMachineCommand#REDEFINECLASSES} command. The original and
   * redefined methods are considered equivalent if their bytecodes are the same
   * except for indices into the constant pool and the referenced constants are
   * equal.
   * </p>
   */
  ISOBSOLETE(4) {
    @Override
    public void execute(MethodInfo methodInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      // is ok as far as the redefine classes capability is false
      throw new NotImplementedException();
    }
  },

  /**
   * Returns variable information for the method, including generic signatures
   * for the variables. The variable table includes arguments and locals
   * declared within the method. For instance methods, the "this" reference is
   * included in the table. Also, synthetic variables may be present. Generic
   * signatures are described in the signature attribute section in the Java
   * Virtual Machine Specification, 3rd Edition.
   * 
   * @since JDWP version 1.5
   */
  VARIABLETABLEWITHGENERIC(5) {
    @Override
    public void execute(MethodInfo methodInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      VariableTable variableTable = new VariableTable(methodInfo);
      variableTable.writeGeneric(os);

    }
  };
  private byte commandId;

  private MethodCommand(int commandId) {
    this.commandId = (byte) commandId;
  }

  private static ReverseEnumMap<Byte, MethodCommand> map = new ReverseEnumMap<Byte, MethodCommand>(MethodCommand.class);

  @Override
  public Byte identifier() {
    return commandId;
  }

  @Override
  public MethodCommand convert(Byte val) throws IllegalArgumentException {
    return map.get(val);
  }

  /**
   * The {@link MethodCommand} specific extension of command execution.
   * 
   * @param methodInfo
   *          The method that is associated with the method commands.
   * @param bytes
   *          The buffer of bytes that is used as an input of the command.
   * @param os
   *          The output stream that is used for a command output.
   * @param contextProvider
   *          The Context Provider.
   * @throws IOException
   *           If given input or output have I/O issues.
   * @throws JdwpException
   *           If any JDWP based error occurs.
   */
  public abstract void execute(MethodInfo methodInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException;

  @Override
  public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
    ReferenceTypeId refId = contextProvider.getObjectManager().readReferenceTypeId(bytes);
    ClassInfo clazz = refId.get();

    execute(VirtualMachineHelper.getClassMethod(clazz, bytes.getLong()), bytes, os, contextProvider);
  }
}