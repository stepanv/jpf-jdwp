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
import gov.nasa.jpf.jdwp.exception.id.InvalidIdentifierException;
import gov.nasa.jpf.jdwp.id.TaggableIdentifierBase;
import gov.nasa.jpf.jdwp.id.object.special.NullReferenceId;
import gov.nasa.jpf.vm.ClassInfo;

/**
 * The base implementation of {@link ReferenceTypeId} representation of
 * <tt>referenceTypeID</tt> common data type from the JDWP Specification.
 * 
 * @author stepan
 * 
 */
public abstract class ReferenceTypeIdBase extends TaggableIdentifierBase<ClassInfo> implements ReferenceTypeId {

  private TypeTag typeTag;

  /**
   * Reference Type ID constructor.
   * 
   * @param id
   *          The numerical ID of this identifier.
   * @param classInfo
   *          The {@link ClassInfo} that stands for the desired reference type.
   */
  protected ReferenceTypeIdBase(TypeTag typeTag, long id, ClassInfo classInfo) {
    super(id, classInfo);
    this.typeTag = typeTag;
  }

  /**
   * Constructs the {@link ReferenceTypeId} or some of its subtypes for the
   * given class.
   * 
   * @param id
   *          The desired ID that will identify the given class across the JDWP.
   * @param classInfo
   *          The class representation to create the reference ID for.
   * @return The reference ID.
   */
  public static ReferenceTypeId factory(long id, ClassInfo classInfo) {
    if (classInfo.isArray()) {
      return new ArrayTypeReferenceIdImpl(id, classInfo);
    }
    if (classInfo.isInterface()) {
      return new InterfaceTypeReferenceIdImpl(id, classInfo);
    }

    return new ClassTypeReferenceIdImpl(id, classInfo);
  }

  @Override
  public IdentifiableEnum<Byte> getIdentifier() {
    return typeTag;
  }

  @Override
  public ClassInfo nullObjectHandler() throws InvalidIdentifierException {
    return NullReferenceId.getInstance().get();
  }

  /**
   * The simple algorithm to determine what this reference ID stands for.
   * 
   * @param typeTag
   *          The type tag to test for.
   * @return Whether the type of this instance equals to the given type tag.
   */
  private boolean is(TypeTag typeTag) {
    return typeTag.equals(this.typeTag);
  }

  @Override
  public boolean isArrayType() {
    return is(TypeTag.ARRAY);
  }

  @Override
  public boolean isClassType() {
    return is(TypeTag.CLASS);
  }

  @Override
  public boolean isInterfaceType() {
    return is(TypeTag.INTERFACE);
  }

}
