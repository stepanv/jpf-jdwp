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

package gov.nasa.jpf.jdwp.id;

import gov.nasa.jpf.jdwp.exception.id.InvalidFieldIdException;
import gov.nasa.jpf.jdwp.exception.id.InvalidIdentifierException;
import gov.nasa.jpf.vm.FieldInfo;

/**
 * This class implements the corresponding <code>fieldID</code> common data type
 * from the JDWP Specification.
 * 
 * <p>
 * <h2>JDWP Specification</h2>
 * Uniquely identifies a field in some class in the target VM. The fieldID must
 * uniquely identify the field within its class/interface or any of its
 * subclasses/subinterfaces/implementors. A fieldID is not necessarily unique on
 * its own; it is always paired with a referenceTypeID to uniquely identify one
 * field. The referenceTypeID can identify either the declaring type of the
 * field or a subtype.
 * </p>
 * 
 * @author stepan
 * 
 */
public class FieldId extends IdentifierBase<FieldInfo> {

  /**
   * Field ID constructor.
   * 
   * @param id
   *          The numerical ID of this identifier.
   * @param fieldInfo
   *          The {@link FieldInfo} this identifier is created for.
   */
  public FieldId(Long id, FieldInfo fieldInfo) {
    super(id, fieldInfo);
  }

  @Override
  public FieldInfo nullObjectHandler() throws InvalidIdentifierException {
    throw new InvalidFieldIdException(this);
  }

}
