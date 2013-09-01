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

import gov.nasa.jpf.JPF.Status;
import gov.nasa.jpf.jdwp.exception.InternalException;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.VmDead;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum CommandSet implements ConvertibleEnum<Byte, CommandSet> {

  /**
   * Virtual machine command set.
   */
  VIRTUALMACHINE(1, VirtualMachineCommand.ALLCLASSES),

  /**
   * Reference type command set.
   */
  REFERENCETYPE(2, ReferenceTypeCommand.class),

  /**
   * Class type command set.
   */
  CLASSTYPE(3, ClassTypeCommand.INVOKEMETHOD),

  /**
   * Array type command set.
   */
  ARRAYTYPE(4, ArrayTypeCommand.NEWINSTANCE),

  /**
   * Interface type command set.
   */
  INTERFACETYPE(5, InterfaceTypeCommand.class),

  /**
   * Method command set.
   */
  METHOD(6, MethodCommand.BYTECODES),

  /**
   * Field command set.
   */
  FIELD(8, FieldCommand.class),

  /**
   * Object reference command set.
   */
  OBJECTREFERENCE(9, ObjectReferenceCommand.DISABLECOLLECTION),

  /**
   * String reference command set.
   */
  STRINGREFERENCE(10, StringReferenceCommand.VALUE),

  /**
   * Thread reference command set.
   */
  THREADREFERENCE(11, ThreadReferenceCommand.NAME),

  /**
   * Thread group reference command set.
   */
  THREADGROUPREFERENCE(12, ThreadGroupReferenceCommand.CHILDREN),

  /**
   * Array reference command set.
   */
  ARRAYREFERENCE(13, ArrayReferenceCommand.GETVALUES),

  /**
   * Class loader reference command set.
   */
  CLASSLOADERREFERENCE(14, ClassLoaderReferenceCommand.VISIBLECLASSES),

  /**
   * Event request command set.
   */
  EVENTREQUEST(15, EventRequestCommand.CLEAR),

  /**
   * Stack frame command set.
   */
  STACKFRAME(16, StackFrameCommand.GETVALUES),

  /**
   * Class object reference command set.
   */
  CLASSOBJECTREFERENCE(17, ClassObjectReferenceCommand.REFLECTEDTYPE),

  /**
   * Event command set.
   */
  EVENT(64, EventCommand.COMPOSITE);

  private static ReverseEnumMap<Byte, CommandSet> map = new ReverseEnumMap<Byte, CommandSet>(CommandSet.class);

  private byte commandSetId;

  private ConvertibleEnum<Byte, ? extends Command> commandConverterSample;

  public ConvertibleEnum<Byte, ? extends Command> getCommandConverterSample() {
    return commandConverterSample;
  }

  private CommandSet(int commandSetId, ConvertibleEnum<Byte, ? extends Command> commandConverterSample) {
    this.commandSetId = (byte) commandSetId;
    this.commandConverterSample = commandConverterSample;
  }

  private CommandSet(int commandSetId, Class<? extends ConvertibleEnum<Byte, ? extends Command>> commandConverterSample) {
    this.commandSetId = (byte) commandSetId;
    this.commandConverterSample = commandConverterSample.getEnumConstants()[0];
  }

  @Override
  public Byte identifier() {
    return commandSetId;
  }

  @Override
  public CommandSet convert(Byte val) throws JdwpError {
    return map.get(val);
  }

  final static Logger logger = LoggerFactory.getLogger(CommandSet.class);

  public static void execute(Command command, ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider)
      throws IOException, JdwpError {

    // this will help to not mask the eventual exception thrown in the try
    // block
    Throwable chainError = null;

    try {
      logger.info("Running command: {} (class: {})", command, command.getClass());
      contextProvider.getVirtualMachine().getRunLock().lock();
      command.execute(bytes, os, contextProvider);
    } catch (RuntimeException e) {
      logger.error("Fatal error occured during the execution of command: {} (class: {})", command, command.getClass(), e);
      chainError = e;
    } catch (Error e) {
      logger.error("Fatal error occured during the execution of command: {} (class: {})", command, command.getClass(), e);
      chainError = e;
    } catch (JdwpError e) {
      chainError = e;
    } finally {
      contextProvider.getVirtualMachine().getRunLock().unlock();

      // This is how we detect JPF has terminated
      if (contextProvider.getJPF().getStatus() == Status.DONE) {

        String errorString = "JPF execution stopped while running command: " + command + " of set: " + command.getClass();
        // If JVM has terminated we want to return VM_DEAD error code
        // rather than anything else
        if (chainError != null) {
          // we still want to keep track of the error
          throw new VmDead(errorString, chainError);
        } else {
          throw new VmDead(errorString);
        }
      }

      if (chainError != null) {
        if (chainError instanceof JdwpError) {
          throw (JdwpError) chainError;
        } else if (chainError instanceof Error) {
          throw (Error) chainError;
        } else if (chainError instanceof RuntimeException) {
          throw (RuntimeException) chainError;
        } else {
          throw new InternalException(chainError);
        }
      }
    }
  }

}