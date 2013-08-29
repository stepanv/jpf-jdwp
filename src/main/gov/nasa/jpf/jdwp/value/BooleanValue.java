package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class implements corresponding primitive <i>value</i> common data type
 * of tag <i>boolean</i> (implemented as {@link Tag#BOOLEAN}) according to the
 * JDWP Specification as stated in the table of Detailed Command Information
 * section.
 * 
 * @see Tag
 * @see PrimitiveValue
 * 
 * @author stepan
 * 
 */
public class BooleanValue extends PrimitiveValue {

  private boolean value;

  public BooleanValue(boolean value) {
    super(Tag.BOOLEAN);
    this.value = value;
  }

  @Override
  public void writeUntagged(DataOutputStream os) throws IOException {
    os.writeBoolean(value);
  }

  @Override
  public void push(StackFrame frame) {
    frame.push(value ? 1 : 0);
  }

  @Override
  public void modify(StackFrame stackFrame, int slotIndex) {
    stackFrame.setLocalVariable(slotIndex, value ? 1 : 0, false);
  }

  @Override
  public void modify(ElementInfo instance, FieldInfo field) {
    instance.setBooleanField(field, value);
  }

  @Override
  public void modify(ElementInfo arrayInstance, int index) {
    arrayInstance.setBooleanElement(index, value);
  }

}
