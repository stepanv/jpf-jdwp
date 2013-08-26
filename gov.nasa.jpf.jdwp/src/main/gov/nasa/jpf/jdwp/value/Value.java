package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.command.VirtualMachineCommand;
import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.id.Identifier;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class implements corresponding <i>value</i> common data type from the
 * JDWP specification.<br/>
 * By default the <i>value</i> is sent across JDWP including the {@link Tag}
 * byte. To the contrary, standard IDs (subclasses of {@link Identifier}) are
 * sent by default without the {@link Tag} byte. To avoid confusion
 * {@link Value} doesn't declare <tt>write</tt> method and thus a developer must
 * decide between {@link Value#writeTagged(DataOutputStream)} and
 * {@link Value#writeUntagged(DataOutputStream)} explicitly, according to the
 * specification, when writing the <i>value</i> to the stream.
 * 
 * <p>
 * <h2>JDWP Specification</h2>
 * A value retrieved from the target VM. The first byte is a signature byte
 * which is used to identify the type. See {@link Tag} for the possible values
 * of this byte. It is followed immediately by the value itself. This value can
 * be an {@link ObjectId} (see Get ID Sizes (
 * {@link VirtualMachineCommand#IDSIZES})) or a primitive value (1 to 8 bytes)
 * {@link PrimitiveValue}.<br/>
 * More details about each value type can be found in the next table.
 * </p>
 * 
 * <p>
 * Note that specification refers to "some next table" which sadly does not
 * exist.
 * </p>
 * 
 * @see PrimitiveValue
 * @see ObjectId
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
   *          Output stream
   * @throws IOException
   *           If I/O error occurs
   */
  public void writeTagged(DataOutputStream os) throws IOException;

  /**
   * Writes the plain value to the output stream.<br/>
   * Values are written untagged rarely, as the JDWP specification states, if
   * the signature is known from the context as it is with arrays.
   * 
   * @param os
   * @throws IOException
   */
  public void writeUntagged(DataOutputStream os) throws IOException;

  /**
   * Pushes the current value (of this instance) to the given frame.
   * 
   * @param frame
   *          The stack frame where to push this value.
   * @throws InvalidObject
   */
  public void push(StackFrame frame);

  /**
   * Modifies local variable at the given slot index of the given frame.
   * 
   * @param stackFrame
   *          The frame where to modify the local variable. Must be modifiable!
   * @param slotIndex
   *          The slot index of the local variable.
   */
  public void modify(StackFrame stackFrame, int slotIndex);

  /**
   * Modifies the field of given instance with its value.
   * <p>
   * Note that this method is not interchangeable with
   * {@link Value#modify(ElementInfo, int)} as it is designed for standard
   * object instances and NOT arrays!
   * 
   * @see Value#modify(ElementInfo, int)
   * 
   * @param instance
   *          The SuT instance to be modified. Must be modifiable!
   * @param fieldInfo
   *          The field that is modified
   */
  public void modify(ElementInfo instance, FieldInfo field);

  /**
   * Modifies the given array at the given index.
   * <p>
   * Note that this method is not interchangeable with
   * {@link Value#modify(ElementInfo, FieldInfo)} as it is designed for arrays
   * only.<br/>
   * It is sad JPF doesn't make a difference between array and standard object
   * instances at the class design level.
   * </p>
   * 
   * @param arrayInstance
   *          The instance of an array to be modified. Must be modifiable!
   * @param index
   *          The index to the array where to modify its value.
   */
  public void modify(ElementInfo arrayInstance, int index);
}
