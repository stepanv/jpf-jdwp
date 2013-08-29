package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class implements corresponding primitive <i>value</i> common data type
 * of tag <i>long</i> (implemented as {@link Tag#LONG}) according to the JDWP
 * Specification as stated in the table of Detailed Command Information section.
 * 
 * @see Tag
 * @see PrimitiveValue
 * 
 * @author stepan
 * 
 */
public class LongValue extends PrimitiveValue {

  private long value;

  public LongValue(long value) {
    super(Tag.LONG);
    this.value = value;
  }

  @Override
  public void writeUntagged(DataOutputStream os) throws IOException {
    os.writeLong(value);
  }

  @Override
  public void push(StackFrame frame) {
    frame.pushLong(value);
  }

  @Override
  public void modify(StackFrame stackFrame, int slotIndex) {
    stackFrame.setLongLocalVariable(slotIndex, value);
  }

  @Override
  public void modify(ElementInfo instance, FieldInfo field) {
    instance.setLongField(field, value);
  }

  @Override
  public void modify(ElementInfo arrayInstance, int index) {
    arrayInstance.setLongElement(index, value);
  }

}
