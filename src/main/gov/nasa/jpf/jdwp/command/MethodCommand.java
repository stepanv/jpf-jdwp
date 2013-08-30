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

import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.util.LineTable;
import gov.nasa.jpf.jdwp.util.VariableTable;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.MethodInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

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
    public void execute(MethodInfo methodInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {
      LineTable lineTable = new LineTable(methodInfo);
      lineTable.write(os);
    }
  },

  /**
   * Returns variable information for the method. The variable table includes
   * arguments and locals declared within the method. For instance methods, the
   * "this" reference is included in the table. Also, synthetic variables may be
   * present.
   */
  VARIABLETABLE(2) {
    @Override
    public void execute(MethodInfo methodInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {
      VariableTable variableTable = new VariableTable(methodInfo);
      variableTable.write(os);
    }
  },
  BYTECODES(3) {
    @Override
    public void execute(MethodInfo methodInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {
      throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

    }
  },
  ISOBSOLETE(4) {
    @Override
    public void execute(MethodInfo methodInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {
      throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

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
    public void execute(MethodInfo methodInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {
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
  public MethodCommand convert(Byte val) throws JdwpError {
    return map.get(val);
  }

  public abstract void execute(MethodInfo methodInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
      throws IOException, JdwpError;

  @Override
  public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
    ReferenceTypeId refId = contextProvider.getObjectManager().readReferenceTypeId(bytes);
    ClassInfo clazz = refId.get();

    execute(VirtualMachineHelper.getClassMethod(clazz, bytes.getLong()), bytes, os, contextProvider);
  }
}