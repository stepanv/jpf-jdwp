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

import gov.nasa.jpf.jdwp.exception.JdwpException;
import gov.nasa.jpf.jdwp.exception.IllegalArgumentException;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadGroupId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.id.object.special.NullObjectId;
import gov.nasa.jpf.jdwp.value.JdwpString;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.jdwp.value.ValueUtils;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ThreadGroupReferenceCommand} enum class implements the
 * {@link CommandSet#THREADGROUPREFERENCE} set of commands. For the detailed
 * specification refer to <a href=
 * "http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#JDWP_ThreadGroupReference"
 * >http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#
 * JDWP_ThreadGroupReference</a> JDWP 1.6 Specification pages.
 * 
 * @author stepan
 * 
 */
public enum ThreadGroupReferenceCommand implements Command, ConvertibleEnum<Byte, ThreadGroupReferenceCommand> {

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the thread group name.
   * </p>
   */
  NAME(1) {
    @Override
    public void execute(ElementInfo threadGroupElementInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      int nameref = threadGroupElementInfo.getReferenceField("name");
      ElementInfo name = contextProvider.getVM().getHeap().get(nameref);
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
    public void execute(ElementInfo threadGroupElementInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      int parentref = threadGroupElementInfo.getReferenceField("parent");
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

  /**
   * <p>
   * <h2>JDWP Specification</h2>
   * Returns the live threads and active thread groups directly contained in
   * this thread group. Threads and thread groups in child thread groups are not
   * included. A thread is alive if it has been started and has not yet been
   * stopped. See {@link ThreadGroup} for information about active ThreadGroups.
   * </p>
   */
  CHILDREN(3) {
    @Override
    public void execute(ElementInfo threadGroupElementInfo, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {

      // we need to find out the number of non-null threads and non-null groups
      // firlst
      ByteArrayOutputStream childrenOs = new ByteArrayOutputStream(0);
      DataOutputStream childrenDos = new DataOutputStream(childrenOs);

      // get the threads first
      int threadsref = threadGroupElementInfo.getReferenceField("threads");
      ElementInfo threads = contextProvider.getVM().getHeap().get(threadsref);

      int childThreads = 0;

      for (int i = 0; i < threads.arrayLength(); i++) {
        Value value = ValueUtils.arrayIndexToValue(threads, i);
        if (value instanceof ThreadId && !((ThreadId) value).isNull()) {
          ThreadInfo thread = ((ThreadId) value).getInfoObject();
          if (thread.isAlive()) {
            logger.debug("Adding a thread {}", thread);
            value.writeUntagged(childrenDos);
            childThreads++;
          }
        }
      }
      os.writeInt(childThreads);
      os.write(childrenOs.toByteArray());

      childrenOs = new ByteArrayOutputStream(0);
      childrenDos = new DataOutputStream(childrenOs);

      int groupsref = threadGroupElementInfo.getReferenceField("groups");
      ElementInfo groups = contextProvider.getVM().getHeap().get(groupsref);

      int childGroups = 0;
      for (int i = 0; i < groups.arrayLength(); i++) {
        // from the ThreadGroup implementation it seems all groups are active
        // (if the VM is not running which is a JPF case)
        Value value = ValueUtils.arrayIndexToValue(groups, i);
        if (value instanceof ThreadGroupId && !((ThreadGroupId) value).isNull()) {
          value.writeUntagged(childrenDos);
          childGroups++;
        }
      }
      os.writeInt(childGroups);
      os.write(childrenOs.toByteArray());

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
  public ThreadGroupReferenceCommand convert(Byte val) throws IllegalArgumentException {
    return map.get(val);
  }

  @Override
  public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
    ObjectId oid = contextProvider.getObjectManager().readObjectId(bytes);
    execute(oid.get(), bytes, os, contextProvider);
  }

  /**
   * The thread group specific implementation of the command.
   * 
   * @param threadGroupElementInfo
   *          The thread group reference.
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
  public abstract void execute(ElementInfo threadGroupElementInfo, ByteBuffer bytes, DataOutputStream os,
                               CommandContextProvider contextProvider) throws IOException, JdwpException;
}