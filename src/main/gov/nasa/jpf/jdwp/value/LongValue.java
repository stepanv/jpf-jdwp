/* 
   Copyright (C) 2013 Stepan Vavra

This file is part of (Java Debug Wire Protocol) JDWP for 
Java PathFinder (JPF) project.

JDWP for JPF is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

JDWP for JPF is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 
 */

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

  /**
   * Constructs the long value for the given parameter.
   * 
   * @param value
   *          The desired value of this <i>Value</i>.
   */
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
