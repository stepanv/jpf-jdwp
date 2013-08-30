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

package gov.nasa.jpf.jdwp.event.filter;

import gov.nasa.jpf.jdwp.event.FieldOnlyFilterable;
import gov.nasa.jpf.jdwp.exception.InvalidIdentifier;
import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.vm.FieldInfo;

/**
 * <p>
 * Can be used with {@link FieldOnlyFilterable} events.
 * </p>
 * <p>
 * <h2>JDWP Specification</h2>
 * Restricts reported events to those that occur for a given field. This
 * modifier can be used with field access and field modification event kinds
 * only.
 * </p>
 * TODO not done yet!
 * 
 * @author stepan
 * 
 */
public class FieldOnlyFilter extends Filter<FieldOnlyFilterable> {

  ReferenceTypeId declaring;
  FieldId fieldId;

  /**
   * Creates Field Only filter.
   * 
   * @param declaring
   *          Type in which field is declared.
   * @param fieldId
   *          Required field
   */
  public FieldOnlyFilter(ReferenceTypeId declaring, FieldId fieldId) {
    super(ModKind.FIELD_ONLY, FieldOnlyFilterable.class);
    this.declaring = declaring;
    this.fieldId = fieldId;
  }

  @Override
  public boolean matches(FieldOnlyFilterable event) {
    try {
      FieldInfo fieldInfo = fieldId.get();
      return fieldInfo == event.getFieldInfo();
    } catch (InvalidIdentifier e) {
      // if fieldId is not resolvable, this filter is not effective
      // anymore
      return false;
    }
  }

}
