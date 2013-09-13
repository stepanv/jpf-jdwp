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

import gov.nasa.jpf.jdwp.JdwpConstants;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadGroupId;
import gov.nasa.jpf.jdwp.id.object.special.NullObjectId;
import gov.nasa.jpf.jdwp.value.JdwpString;
import gov.nasa.jpf.vm.ElementInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ThreadGroupReferenceCommand implements Command, ConvertibleEnum<Byte, ThreadGroupReferenceCommand> {

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the thread group name.
   * </p>
   */
  NAME(1) {
    @Override
    public void execute(ElementInfo threadGroupElementInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {
      int nameref = threadGroupElementInfo.getReferenceField(JdwpConstants.FIELDNAME_THREADGROUP_NAME);
      ElementInfo name = contextProvider.getVirtualMachine().getJpf().getVM().getHeap().get(nameref);
      JdwpString.write(name.asString(), os);
    }
  },

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the thread group, if any, which contains a given thread group.
   * </p>
   */
  PARENT(2) {
    @Override
    public void execute(ElementInfo threadGroupElementInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {
      int parentref = threadGroupElementInfo.getReferenceField(JdwpConstants.FIELDNAME_THREADGROUP_PARENT);
      ElementInfo parent = contextProvider.getVM().getHeap().get(parentref);
      logger.debug("Thread group parent: {}", parent);

      if (parent == null) {
        NullObjectId.instantWrite(os);
      } else {
        ThreadGroupId parentGroup = contextProvider.getObjectManager().getThreadGroupId(parent);
        parentGroup.write(os);
      }
    }
  },
  CHILDREN(3) {
    @Override
    public void execute(ElementInfo threadGroupElementInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
        throws IOException, JdwpError {
      throw new JdwpError(ErrorType.NOT_IMPLEMENTED);

    }
  };

  final static Logger logger = LoggerFactory.getLogger(ThreadGroupReferenceCommand.class);

  private byte commandId;

  private ThreadGroupReferenceCommand(int commandId) {
    this.commandId = (byte) commandId;
  }

  private static ReverseEnumMap<Byte, ThreadGroupReferenceCommand> map = new ReverseEnumMap<Byte, ThreadGroupReferenceCommand>(
      ThreadGroupReferenceCommand.class);

  @Override
  public Byte identifier() {
    return commandId;
  }

  @Override
  public ThreadGroupReferenceCommand convert(Byte val) throws JdwpError {
    return map.get(val);
  }

  @Override
  public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpError {
    ObjectId oid = contextProvider.getObjectManager().readObjectId(bytes);
    execute(oid.get(), bytes, os, contextProvider);
  }

  public abstract void execute(ElementInfo threadGroupElementInfo, ByteBuffer bytes, DataOutputStream os,
                               CommandContextProvider contextProvider) throws IOException, JdwpError;
}