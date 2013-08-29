package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.command.VirtualMachineCommand;
import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
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
 * 
 * 
 * @see VirtualMachineCommand#CAPABILITIESNEW
 * @author stepan
 * 
 */
public class FieldModificationEvent extends LocatableEvent implements LocationOnlyFilterable, FieldOnlyFilterable {

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

  @Override
  protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
    JdwpObjectManager objectManager = JdwpObjectManager.getInstance();

    // TODO this is not according the spec!
    ClassInfo fieldObjectClassInfo = fieldInfo.getClassInfo();

    ReferenceTypeId referenceTypeId = objectManager.getReferenceTypeId(fieldObjectClassInfo);
    referenceTypeId.writeTagged(os);

    FieldId fieldId = objectManager.getFieldId(fieldInfo);
    fieldId.write(os);

    ObjectId objectId = objectManager.getObjectId(objectBeingModified);
    objectId.writeTagged(os);

    // get the value now
    // it wasn't needed sooner
    Value value = tag.peekValue(stackFrame);
    value.writeTagged(os);
  }

  @Override
  public FieldInfo getFieldInfo() {
    return fieldInfo;
  }

  @Override
  public String toString() {
    return super.toString() + ", field: " + fieldInfo;
  }

}
