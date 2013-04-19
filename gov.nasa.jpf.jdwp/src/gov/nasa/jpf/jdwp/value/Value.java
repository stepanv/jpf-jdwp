package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.command.VirtualMachineCommand;
import gov.nasa.jpf.jdwp.id.object.ObjectId;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>
 * <h2>JDWP Specification</h2>
 * A value retrieved from the target VM. The first byte is a signature byte
 * which is used to identify the type. See {@link Tag} for the possible values
 * of this byte. It is followed immediately by the value itself. This value can
 * be an {@link ObjectId} (see Get ID Sizes (
 * {@link VirtualMachineCommand#IDSIZES})) or a primitive value (1 to 8 bytes).<br/>
 * More details about each value type can be found in the next table.
 * </p>
 * 
 * @author stepan
 * 
 */
public interface Value {
	public void write(DataOutputStream os) throws IOException;

	public void writeTagged(DataOutputStream os) throws IOException;

}
