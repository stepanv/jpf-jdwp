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
import gov.nasa.jpf.jdwp.id.JdwpIdManager;
import gov.nasa.jpf.jdwp.id.object.ClassLoaderId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ClassLoaderReferenceCommand} enum class implements the
 * {@link CommandSet#CLASSLOADERREFERENCE} set of commands. For the detailed
 * specification refer to <a href=
 * "http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#JDWP_ClassLoaderReference"
 * >http://docs.oracle.com/javase/6/docs/platform/jpda/jdwp/jdwp-protocol.html#
 * JDWP_ClassLoaderReference</a> JDWP 1.6 Specification pages.
 * 
 * @author stepan
 * 
 */
public enum ClassLoaderReferenceCommand implements Command, ConvertibleEnum<Byte, ClassLoaderReferenceCommand> {

  /**
   * <h2>JDWP Specification</h2>
   * <p>
   * Returns a list of all classes which this class loader has been requested to
   * load. This class loader is considered to be an initiating class loader for
   * each class in the returned list. The list contains each reference type
   * defined by this loader and any types for which loading was delegated by
   * this class loader to another class loader.
   * </p>
   * <p>
   * The visible class list has useful properties with respect to the type
   * namespace. A particular type name will occur at most once in the list. Each
   * field or variable declared with that type name in a class defined by this
   * class loader must be resolved to that single type.
   * </p>
   * <p>
   * No ordering of the returned list is guaranteed.
   * </p>
   */
  VISIBLECLASSES(1) {
    @Override
    public void execute(ByteBuffer bytes, DataOutputStream os, CommandContextProvider contextProvider) throws IOException, JdwpException {
      JdwpIdManager objectManager = contextProvider.getObjectManager();

      ClassLoaderId classLoaderId = objectManager.readClassLoaderId(bytes);

      ClassLoaderInfo classLoaderInfo;
      if (classLoaderId.isNull()) {
        // system class loader
        classLoaderInfo = ThreadInfo.getCurrentThread().getSystemClassLoaderInfo();
      } else {
        classLoaderInfo = classLoaderId.getClassLoaderInfo();
      }

      logger.debug("Using classloader: {}", classLoaderInfo);

      // we need to write the number of classes first
      ByteArrayOutputStream loadedClassesOutputBytes = new ByteArrayOutputStream(0);
      DataOutputStream loadedClassesOS = new DataOutputStream(loadedClassesOutputBytes);

      int classes = 0;

      for (Iterator<ClassInfo> loadedClasses = classLoaderInfo.iterator(); loadedClasses.hasNext();) {
        ClassInfo classInfo = loadedClasses.next();
        // get the reference type for the class
        ReferenceTypeId referenceTypeId = objectManager.getReferenceTypeId(classInfo);
        referenceTypeId.writeTagged(loadedClassesOS);

        logger.debug("Class found: {}", referenceTypeId);

        // increase the number of classes
        ++classes;
      }
      logger.trace("About to send classes (number: {}) as bytes: {}", classes, loadedClassesOutputBytes.toByteArray());

      os.writeInt(classes);
      os.write(loadedClassesOutputBytes.toByteArray());

      logger.trace("Command end");

    }
  };

  final static Logger logger = LoggerFactory.getLogger(ClassLoaderReferenceCommand.class);

  private byte commandId;

  private ClassLoaderReferenceCommand(int commandId) {
    this.commandId = (byte) commandId;
  }

  private static ReverseEnumMap<Byte, ClassLoaderReferenceCommand> map = new ReverseEnumMap<Byte, ClassLoaderReferenceCommand>(
      ClassLoaderReferenceCommand.class);

  @Override
  public Byte identifier() {
    return commandId;
  }

  @Override
  public ClassLoaderReferenceCommand convert(Byte val) throws IllegalArgumentException {
    return map.get(val);
  }

}