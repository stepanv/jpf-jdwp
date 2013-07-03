package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.command.VirtualMachineCommand;
import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.Fields;
import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class implements corresponding value from the JDWP specification.<br/>
 * By default value is sent across JDWP including the {@link Tag} byte. To the
 * contrary, standard IDs are sent by default without the {@link Tag} byte. To
 * avoid confusion {@link Value} doesn't declare <tt>write</tt> method and thus
 * a developer must decide between {@link Value#writeTagged(DataOutputStream)}
 * and {@link Value#writeUntagged(DataOutputStream)} explicitly, according to
 * the specification.
 * 
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
	/**
	 * Writes the value including the {@link Tag} as a first byte which is a
	 * signature.<br/>
	 * Values are written tagged by default.
	 * 
	 * @param os
	 *            Output stream
	 * @throws IOException
	 *             If I/O error occurs
	 */
	public void writeTagged(DataOutputStream os) throws IOException;

	/**
	 * Writes the plain value to the output stream.<br/>
	 * Values are written untagged rarely, as the JDWP specification states, if the
	 * signature is known from the context as it is with arrays.
	 * 
	 * @param os
	 * @throws IOException
	 */
	public void writeUntagged(DataOutputStream os) throws IOException;

	public void push(StackFrame frame) throws InvalidObject;

	public void modify(Fields fields, int index) throws InvalidObject;

	public void modify(StackFrame stackFrame, int slotIndex) throws InvalidObject;
}
