package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.JdwpObjectManager;
import gov.nasa.jpf.jdwp.command.VirtualMachineCommand;
import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.id.object.special.NullObjectId;
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
		JdwpObjectManager.getInstance().getReferenceTypeId(fieldType).writeTagged(os);
		fieldId.write(os);
		object.writeTagged(os);
		value.write(os);
	}

	@Override
	public FieldId getFieldId() {
		return fieldId;
	}

}
