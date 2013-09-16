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

import gov.nasa.jpf.jdwp.command.IdentifiableEnum;
import gov.nasa.jpf.jdwp.id.TaggableIdentifier;
import gov.nasa.jpf.vm.ClassInfo;

/**
 * This interface represents the corresponding <code>referenceTypeID</code>
 * common data type from the JDWP Specification.
 * 
 * <p>
 * <h2>JDWP Specification:</h2>
 * Uniquely identifies a reference type in the target VM. It should not be
 * assumed that for a particular class, the <tt>classObjectID</tt> and the
 * <tt>referenceTypeID</tt> are the same. A particular reference type will be
 * identified by exactly one ID in JDWP commands and replies throughout its
 * lifetime A referenceTypeID is not reused to identify a different reference
 * type, regardless of whether the referenced class has been unloaded.
 * </p>
 * 
 * @author stepan
 * 
 */
public interface ReferenceTypeId extends TaggableIdentifier<ClassInfo> {

  public enum TypeTag implements IdentifiableEnum<Byte> {

    /** ReferenceType is a class. */
    CLASS(1),

    /** ReferenceType is an interface. */
    INTERFACE(2),

    /** ReferenceType is an array. */
    ARRAY(3);

    private byte typeTagId;

    /**
     * Constructs {@link TypeTag} Type tag identifier.
     * 
     * @param typeTagId
     *          The type tag ID.
     */
    TypeTag(int typeTagId) {
      this.typeTagId = (byte) typeTagId;
    }

    @Override
    public Byte identifier() {
      return typeTagId;
    }

  }

  /**
   * Whether this reference type represents an array.
   * 
   * @return true or false
   */
  boolean isArrayType();

  /**
   * Whether this reference type represents a class.
   * 
   * @return true or false
   */
  boolean isClassType();

  /**
   * Whether this reference type represents an interface.
   * 
   * @return true or false
   */
  boolean isInterfaceType();

}
