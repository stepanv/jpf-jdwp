package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class implements corresponding primitive <i>value</i> common data type
 * of tag <i>short</i> (implemented as {@link Tag#SHORT}) according to the JDWP
 * Specification as stated in the table of Detailed Command Information section.
 * 
 * @see Tag
 * @see PrimitiveValue
 * 
 * @author stepan
 * 
 */
public class ShortValue extends PrimitiveValue {

  private short value;

  public ShortValue(short value) {
    super(Tag.SHORT);
    this.value = value;
  }

  @Override
  public void writeUntagged(DataOutputStream os) throws IOException {
    os.writeShort(value);
  }

  @Override
  public void push(StackFrame frame) {
    frame.push(value);
  }

  @Override
  public void modify(StackFrame stackFrame, int slotIndex) {
    stackFrame.setLocalVariable(slotIndex, value, false);
  }

  @Override
  public void modify(ElementInfo instance, FieldInfo field) {
    instance.setShortField(field, value);
  }

  @Override
  public void modify(ElementInfo arrayInstance, int index) {
    arrayInstance.setShortElement(index, value);
  }

}
