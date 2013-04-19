package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.JdwpObjectManager;
import gov.nasa.jpf.jdwp.command.VirtualMachineCommand;
import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.id.object.special.NullObjectId;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.vm.ClassInfo;

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
	private FieldId fieldId;
	private ObjectId<?> object;

	/**
	 * 
	 * @param threadId
	 *            Accessing thread
	 * @param location
	 *            Location of access
	 * @param fieldType
	 *            Type of field
	 * @param fieldId
	 *            Field being accessed
	 * @param objectOrNull
	 *            Object being accessed (null=0 for statics)
	 */
	public FieldAccessEvent(ThreadId threadId, Location location, ClassInfo fieldType, FieldId fieldId, ObjectId<?> objectOrNull) {
		super(EventKind.FIELD_ACCESS, threadId, location);
		this.fieldType = fieldType;
		this.fieldId = fieldId;
		this.object = objectOrNull == null ? NullObjectId.getInstance() : objectOrNull;
	}

	@Override
	protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
		JdwpObjectManager.getInstance().getReferenceTypeId(fieldType).writeTagged(os);
		fieldId.write(os);
		object.writeTagged(os);
	}

}
