package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.JdwpObjectManager;
import gov.nasa.jpf.jdwp.command.VirtualMachineCommand;
import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.id.object.special.NullObjectId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.vm.ClassInfo;

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
	private FieldId fieldId;
	private ObjectId object;
	private Value value;

	/**
	 * Creates Field Modification event.
	 * 
	 * @param threadId
	 *            Modifying thread
	 * @param location
	 *            Location of modify
	 * @param fieldType
	 *            Type of field
	 * @param fieldId
	 *            Field being modified
	 * @param objectOrNull
	 *            Object being modified (null=0 for statics)
	 * @param valueToBe
	 *            Value to be assigned
	 */
	public FieldModificationEvent(ThreadId threadId, Location location, ClassInfo fieldType, FieldId fieldId, ObjectId objectOrNull, Value valueToBe) {
		super(EventKind.FIELD_MODIFICATION, threadId, location);
		this.fieldType = fieldType;
		this.fieldId = fieldId;
		this.object = objectOrNull == null ? NullObjectId.getInstance() : objectOrNull;
		this.value = valueToBe;
	}

	@Override
	protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
		ClassInfo fieldObjectClassInfo;
		try {
			fieldObjectClassInfo = fieldId.get().getClassInfo();
			// TODO this is not according the spec!
		} catch (InvalidObject e) {
			throw new IOException(e);
		}
		ReferenceTypeId referenceTypeId = JdwpObjectManager.getInstance().getReferenceTypeId(fieldObjectClassInfo);
		referenceTypeId.writeTagged(os);
		fieldId.write(os);
		object.writeTagged(os);
		
		// TODO JDWP Specification doesn't tell the value has to be tagged
		// Eclipse expects a tagged value!
		// Value is tagged by default - see TODO.txt
		value.writeTagged(os);
	}

	@Override
	public FieldId getFieldId() {
		return fieldId;
	}

	@Override
	public String toString() {
		return super.toString() + ", field: " + fieldId + " valueToBe: " + value;
	}

}
