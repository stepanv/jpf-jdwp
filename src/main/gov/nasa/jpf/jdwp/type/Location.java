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

package gov.nasa.jpf.jdwp.type;

import gov.nasa.jpf.jdwp.command.CommandContextProvider;
import gov.nasa.jpf.jdwp.exception.InvalidLocationException;
import gov.nasa.jpf.jdwp.exception.id.InvalidIdentifierException;
import gov.nasa.jpf.jdwp.exception.id.InvalidMethodIdException;
import gov.nasa.jpf.jdwp.id.JdwpIdManager;
import gov.nasa.jpf.jdwp.id.MethodId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements corresponding <tt>location</tt> JDWP data type from the
 * <i>table of common data types</i> JDWP Specification.
 * <p>
 * <h2>JDWP Specification</h2>
 * An executable location. The location is identified by one byte type tag
 * followed by a a classID followed by a methodID followed by an unsigned
 * eight-byte index, which identifies the location within the method. Index
 * values are restricted as follows:
 * <ul>
 * <li>The index of the start location for the method is less than all other
 * locations in the method.</li>
 * <li>The index of the end location for the method is greater than all other
 * locations in the method.</li>
 * <li>If a line number table exists for a method, locations that belong to a
 * particular line must fall between the line's location index and the location
 * index of the next line in the table.</li>
 * </ul>
 * Index values within a method are monotonically increasing from the first
 * executable point in the method to the last. For many implementations, each
 * byte-code instruction in the method has its own index, but this is not
 * required. <br/>
 * The type tag is necessary to identify whether location's classID identifies a
 * class or an interface. Almost all locations are within classes, but it is
 * possible to have executable code in the static initializer of an interface.
 * </p>
 * 
 * @author stepan
 * 
 */
public class Location {

  final static Logger logger = LoggerFactory.getLogger(Location.class);

  public boolean equals(Location location) {
    if (location == null) {
      return false;
    }
    return instruction == location.instruction;
  }

  private Instruction instruction;
  private MethodInfo methodInfo;
  private int index;

  /**
   * Locations should be create using a factory.
   */
  private Location(MethodInfo methodInfo, int index, Instruction instruction) {
    this.methodInfo = methodInfo;
    this.index = index;
    this.instruction = instruction;
  }

  /**
   * Creates a location for the given instruction.<br/>
   * No check, whether given instruction is possible to transform in a
   * <tt>location</tt>.
   * 
   * @see Location#factorySafe(Instruction, ThreadInfo)
   * 
   * @param instruction
   *          The instruction
   * @return The location
   */
  public static Location factory(Instruction instruction) {
    return new Location(instruction.getMethodInfo(), instruction.getInstructionIndex(), instruction);
  }

  /**
   * Creates a location for the given instruction.<br/>
   * This method tends to be safe but is actually pretty weird.<br/>
   * TODO It must be analyzed when an instruction doesn't have methodInfo or
   * when methodInfo doesn't have a clasInfo and then reflected in this piece of
   * code.
   * 
   * @param instruction
   *          The instruction.
   * @param threadInfo
   *          The thread that executes the given instruction or possibly the
   *          next one.
   * @return The location.
   */
  public static Location factorySafe(Instruction instruction, ThreadInfo threadInfo) {
    while (instruction.getMethodInfo() == null || instruction.getMethodInfo().getClassInfo() == null) {
      instruction = instruction.getNext(threadInfo);
      // TODO possible NPE
    }
    return factory(instruction);
  }

  private static MethodInfo methodInfoLookup(ClassInfo classInfo, long id) throws InvalidMethodIdException {
    logger.debug("looking for METHOD global id: {} of CLASS: {}", id, classInfo);
    for (MethodInfo methodInfo : classInfo.getDeclaredMethodInfos()) {
      if (id == methodInfo.getGlobalId()) {
        logger.trace("METHOD found: {}", methodInfo);
        return methodInfo;
      }
    }
    // also try super types
    if (classInfo.getSuperClass() != null) {
      return methodInfoLookup(classInfo.getSuperClass(), id);
    }
    throw new InvalidMethodIdException(new MethodId(id));
  }

  public static Location factory(ByteBuffer bytes, CommandContextProvider contextProvider) throws InvalidIdentifierException, InvalidLocationException {
    // we don't mix IDs of classes and interfaces and thus this byte is not
    // required
    @SuppressWarnings("unused")
    byte typeTag = bytes.get();
    ReferenceTypeId referenceTypeId = contextProvider.getObjectManager().readReferenceTypeId(bytes);

    ClassInfo classInfo = referenceTypeId.get();

    long id = bytes.getLong();

    long index = bytes.getLong();

    MethodInfo methodInfo = methodInfoLookup(classInfo, id);
    Instruction instruction = methodInfo.getInstruction((int) index);

    if (instruction == null) {
      throw new InvalidLocationException(classInfo, methodInfo, instruction, index);
    }

    return new Location(methodInfo, (int) index, instruction);
  }

  public Instruction getInstruction() {
    return instruction;
  }

  /**
   * Writes this location to the given stream in accordance with the JDWP
   * specification.
   * 
   * @param os
   *          The stream where to write this location.
   * @throws IOException
   *           If I/O error occurs.
   * 
   * @see {@link Location}
   */
  public void write(DataOutputStream os) throws IOException {
    if (methodInfo != null) {

    }
    JdwpIdManager objectManager = JdwpIdManager.getInstance();

    ClassInfo classInfo = methodInfo.getClassInfo();
    objectManager.getReferenceTypeId(classInfo).writeTagged(os);
    os.writeLong(methodInfo.getGlobalId());
    os.writeLong(index);

  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer(super.toString());
    return sb.append(" [instruction: '").append(instruction).append("'], [file: '").append(instruction.getFileLocation())
        .append("'], [methodId: '").append(methodInfo.getGlobalId()).append("'], [index: '").append(index).append("']").toString();
  }
}
