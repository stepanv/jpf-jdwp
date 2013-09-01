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
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.special.NullObjectId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.StaticElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * Notification of a field access in the target VM. Field modifications are not
 * considered field accesses. Requires canWatchFieldAccess capability
 * </p>
 * 
 * @see VirtualMachineCommand#CAPABILITIESNEW
 * @author stepan
 * 
 */
public class FieldAccessEvent extends LocatableEvent implements LocationOnlyFilterable, FieldOnlyFilterable {

  private ClassInfo fieldType;
  private FieldInfo fieldInfo;
  private ElementInfo objectBeingAccessed;

  /**
   * 
   * @param threadInfo
   *          Accessing thread
   * @param location
   *          Location of access
   * @param fieldType
   *          Type of field
   * @param fieldInfo
   *          Field being accessed
   * @param objectBeingAccessed
   *          Object being accessed (will be null=0 for statics when sent across
   *          JDWP)
   */
  public FieldAccessEvent(ThreadInfo threadInfo, Location location, ClassInfo fieldType, FieldInfo fieldInfo,
                          ElementInfo objectBeingAccessed) {
    super(EventKind.FIELD_ACCESS, threadInfo, location);
    this.fieldType = fieldType;
    this.fieldInfo = fieldInfo;
    this.objectBeingAccessed = objectBeingAccessed;
  }

  @Override
  protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
    ClassInfo fieldObjectClassInfo;

    // TODO this is not according the spec!
    fieldObjectClassInfo = fieldInfo.getClassInfo();

    ReferenceTypeId referenceTypeId = JdwpObjectManager.getInstance().getReferenceTypeId(fieldObjectClassInfo);
    referenceTypeId.writeTagged(os);

    FieldId fieldId = JdwpObjectManager.getInstance().getFieldId(fieldInfo);
    fieldId.write(os);

    if (objectBeingAccessed instanceof StaticElementInfo) {
      NullObjectId.instanceWriteTagged(os);
    } else {
      ObjectId objectId = JdwpObjectManager.getInstance().getObjectId(objectBeingAccessed);
      objectId.writeTagged(os);
    }
  }

  @Override
  public FieldInfo getFieldInfo() {
    return fieldInfo;
  }

}