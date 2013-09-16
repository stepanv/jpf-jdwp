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
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.NativeStackFrame;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.Types;

/**
 * Utility class for {@link Value} interface.
 * 
 * @author stepan
 * 
 */
public class ValueUtils {

  /**
   * Only static methods are exposed.
   */
  private ValueUtils() {
  }

  /**
   * Converts the object instance's field to the value.
   * 
   * @param instance
   *          The SuT object instance represented by {@link ElementInfo}
   *          instance.
   * @param field
   *          The field.
   * @return The {@link Value} instance for the given field of the given
   *         instance.
   */
  public static Value fieldToValue(ElementInfo instance, FieldInfo field) {
    Tag tag = Tag.fieldToTag(field);
    return tag.value(instance, field);
  }

  /**
   * Converts the array at the given position to the value.
   * 
   * @param array
   *          The instance of an array in SuT represented by {@link ElementInfo}
   *          instance.
   * @param position
   *          The index to the array.
   * @return The {@link Value} instance for the given array's position.
   */
  public static Value arrayIndexToValue(ElementInfo array, int position) {
    ClassInfo arrayClassInfo = array.getClassInfo().getComponentClassInfo();
    Tag tag = Tag.classInfoToTag(arrayClassInfo);
    return tag.value(array, position);
  }

  /**
   * Gets the {@link Value} instance of the method return value.<br/>
   * No checks whether the method really ended or whether the given frame
   * belongs to the given method execution.
   * 
   * @param method
   *          The method of which return value is requested.
   * @param frame
   *          The frame that was used for the method execution.
   * @return Value (including void) instance of the method return valuef.
   */
  public static Value methodReturnValue(MethodInfo method, StackFrame frame) {
    ClassInfo returnedClassInfo = ClassLoaderInfo.getCurrentResolvedClassInfo(Types.getClassNameFromTypeName(method.getReturnTypeName()));
    Tag returnTag = Tag.classInfoToTag(returnedClassInfo);
    if (frame instanceof NativeStackFrame) {
      // this is very special case for a subset of native methods where
      // frame is not used to store arguments and return values.
      return returnTag.value(((NativeStackFrame) frame).getReturnValue());
    } else {
      return returnTag.peekValue(frame);
    }
  }

}
