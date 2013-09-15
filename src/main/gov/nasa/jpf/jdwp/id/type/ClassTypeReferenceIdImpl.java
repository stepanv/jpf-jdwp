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

package gov.nasa.jpf.jdwp.id.type;

import gov.nasa.jpf.vm.ClassInfo;

/**
 * This class implements the corresponding <code>classID</code> common data type
 * from the JDWP Specification.<br/>
 * These identifiers reference all the {@link Class} subtypes in the target
 * program.
 * 
 * <p>
 * <h2>JDWP Specification:</h2>
 * Uniquely identifies a reference type in the target VM that is known to be a
 * class type.
 * </p>
 * 
 * @author stepan
 */
public class ClassTypeReferenceIdImpl extends ReferenceTypeIdBase {

  /**
   * Class Type ID constructor.
   * 
   * @param id
   *          The numerical ID of this identifier.
   * @param classInfo
   *          The {@link ClassInfo} that stands for the desired class type.
   */
  public ClassTypeReferenceIdImpl(long id, ClassInfo classInfo) {
    super(TypeTag.CLASS, id, classInfo);
  }

}
