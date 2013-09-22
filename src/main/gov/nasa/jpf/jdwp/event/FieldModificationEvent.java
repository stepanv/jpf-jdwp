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

package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.command.VirtualMachineCommand;
import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.JdwpIdManager;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Notification of a field modification in the target VM. Requires
 * canWatchFieldModification capability
 * </p>
 * <p>
 * <h2>Remarks</h2>
 * The specification is not really clear about field modification events.
 * FieldType is actually ThisObjectType.
 * </p>
 * 
 * 
 * @see VirtualMachineCommand#CAPABILITIESNEW
 * @author stepan
 * 
 */
public class FieldModificationEvent extends LocatableEvent implements LocationOnlyFilterable, FieldOnlyFilterable {

  // this is really unused - has to be discussed in my thesis!
  private ClassInfo fieldType;

  private FieldInfo fieldInfo;
  private ElementInfo objectBeingModified;
  private Tag tag;
  private StackFrame stackFrame;

  /**
   * Creates Field Modification event. <br/>
   * In order to postpone use of ID Manager, the value to be assigned is
   * calculated as late as possible using the <tt>tag</tt> and
   * <tt>stackFrame</tt>.
   * 
   * @param threadInfo
   *          Modifying thread
   * @param location
   *          Location of modify
   * @param fieldType
   *          Type of field
   * @param fieldInfo
   *          Field being modified
   * @param objectBeingModified
   *          Object being modified (null=0 for statics)
   * @param tag
   *          The signature of value to-be
   * @param stackFrame
   *          The stack frame where to get the value to-be
   */
  public FieldModificationEvent(ThreadInfo threadInfo, Location location, ClassInfo fieldType, FieldInfo fieldInfo,
                                ElementInfo objectBeingModified, Tag tag, StackFrame stackFrame) {
    super(EventKind.FIELD_MODIFICATION, threadInfo, location);
    this.fieldType = fieldType;
    this.fieldInfo = fieldInfo;
    this.objectBeingModified = objectBeingModified;

    this.tag = tag;
    this.stackFrame = stackFrame;

  }

  private Value resolveValueToBe() {
    return tag.peekValue(stackFrame);
  }

  @Override
  protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
    JdwpIdManager objectManager = JdwpIdManager.getInstance();

    // this is not according the spec!
    ClassInfo fieldObjectClassInfo = fieldInfo.getClassInfo();

    ReferenceTypeId referenceTypeId = objectManager.getReferenceTypeId(fieldObjectClassInfo);
    referenceTypeId.writeTagged(os);

    FieldId fieldId = objectManager.getFieldId(fieldInfo);
    fieldId.write(os);

    ObjectId objectId = objectManager.getObjectId(objectBeingModified);
    objectId.writeTagged(os);

    // get the value now
    // it wasn't needed sooner
    Value value = resolveValueToBe();
    value.writeTagged(os);
  }

  @Override
  public FieldInfo getFieldInfo() {
    return fieldInfo;
  }

  @Override
  public ElementInfo instance() {
    return objectBeingModified;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(" [Field: ").append(fieldInfo).append("], [Object being modified: ").append(objectBeingModified).append("], [Value to be: ").append(resolveValueToBe()).append("]");
    return super.toString() + sb.toString();
  }
}
