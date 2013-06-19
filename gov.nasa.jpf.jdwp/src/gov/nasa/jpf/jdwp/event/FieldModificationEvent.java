package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.command.VirtualMachineCommand;
import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.special.NullObjectId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.jdwp.value.Value;
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
	private Value value;

	/**
	 * Creates Field Modification event.
	 * 
	 * @param threadInfo
	 *            Modifying thread
	 * @param location
	 *            Location of modify
	 * @param fieldType
	 *            Type of field
	 * @param fieldInfo
	 *            Field being modified
	 * @param objectBeingModified
	 *            Object being modified (null=0 for statics)
	 * @param valueToBe
	 *            Value to be assigned
	 */
	public FieldModificationEvent(ThreadInfo threadInfo, Location location, ClassInfo fieldType, FieldInfo fieldInfo, ElementInfo objectBeingModified, Value valueToBe) {
		super(EventKind.FIELD_MODIFICATION, threadInfo, location);
		this.fieldType = fieldType;
		this.fieldInfo = fieldInfo;
		this.objectBeingModified = objectBeingModified;
		this.value = valueToBe;
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
		
		if (objectBeingModified instanceof StaticElementInfo) {
			NullObjectId.instanceWriteTagged(os);
		} else {		
			ObjectId objectId = JdwpObjectManager.getInstance().getObjectId(objectBeingModified);
			objectId.writeTagged(os);
		}
		
		// TODO JDWP Specification doesn't tell the value has to be tagged
		// Eclipse expects a tagged value!
		// Value is tagged by default - see TODO.txt
		value.writeTagged(os);
	}

	@Override
	public FieldInfo getFieldInfo() {
		return fieldInfo;
	}

	@Override
	public String toString() {
		return super.toString() + ", field: " + fieldInfo + " valueToBe: " + value;
	}

}
