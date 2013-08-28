package gov.nasa.jpf.jdwp.value;

import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.StackFrame;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class implements corresponding primitive <i>value</i> common data type
 * of tag <i>double</i> (implemented as {@link Tag#DOUBLE}) according to the
 * JDWP Specification as stated in the table of Detailed Command Information
 * section.
 * 
 * @see Tag
 * @see PrimitiveValue
 * 
 * @author stepan
 * 
 */
public class DoubleValue extends PrimitiveValue {

  private double value;

  public DoubleValue(double value) {
    super(Tag.DOUBLE);
    this.value = value;
  }

  @Override
  public void writeUntagged(DataOutputStream os) throws IOException {
    os.writeDouble(value);
  }

  @Override
  public void push(StackFrame frame) {
    frame.pushDouble(value);
  }

  @Override
  public void modify(StackFrame stackFrame, int slotIndex) {
    stackFrame.setLongLocalVariable(slotIndex, Double.doubleToLongBits(value));
  }

  @Override
  public void modify(ElementInfo instance, FieldInfo field) {
    instance.setDoubleField(field, value);
  }

  @Override
  public void modify(ElementInfo arrayInstance, int index) {
    arrayInstance.setDoubleElement(index, value);
  }
}