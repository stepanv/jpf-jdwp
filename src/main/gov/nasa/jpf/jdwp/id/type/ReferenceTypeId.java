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

import gov.nasa.jpf.jdwp.command.CommandContextProvider;
import gov.nasa.jpf.jdwp.command.ConvertibleEnum;
import gov.nasa.jpf.jdwp.command.IdentifiableEnum;
import gov.nasa.jpf.jdwp.command.ReverseEnumMap;
import gov.nasa.jpf.jdwp.exception.InvalidIdentifier;
import gov.nasa.jpf.jdwp.exception.InvalidReferenceType;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.id.TaggableIdentifier;
import gov.nasa.jpf.vm.ClassInfo;

import java.nio.ByteBuffer;

/**
 * This class implements the corresponding <code>referenceTypeID</code> common
 * data type from the JDWP Specification.
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
 * @param <T>
 */
public class ReferenceTypeId extends TaggableIdentifier<ClassInfo> {

  public enum TypeTag implements ConvertibleEnum<Byte, TypeTag> {
    /** ReferenceType is a class. */
    CLASS(1) {
      @Override
      public ReferenceTypeId createReferenceTypeId(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
        throw new RuntimeException("NOT IMPLEMENTED YET");
      }
    },
    /** ReferenceType is an interface. */
    INTERFACE(2) {
      @Override
      public ReferenceTypeId createReferenceTypeId(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
        throw new RuntimeException("NOT IMPLEMENTED YET");
      }
    },
    /** ReferenceType is an array. */
    ARRAY(3) {
      @Override
      public ReferenceTypeId createReferenceTypeId(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
        throw new RuntimeException("NOT IMPLEMENTED YET");
      }
    };

    private byte typeTagId;

    TypeTag(int typeTagId) {
      this.typeTagId = (byte) typeTagId;
    }

    @Override
    public Byte identifier() {
      return typeTagId;
    }

    private static ReverseEnumMap<Byte, TypeTag> map = new ReverseEnumMap<Byte, ReferenceTypeId.TypeTag>(TypeTag.class);

    @Override
    public TypeTag convert(Byte val) throws JdwpError {
      return map.get(val);
    }

    public abstract ReferenceTypeId createReferenceTypeId(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError;

  }

  private TypeTag typeTag;

  /**
   * Reference Type ID constructor.
   * 
   * @param id
   *          The numerical ID of this identifier.
   * @param classInfo
   *          The {@link ClassInfo} that stands for the desired reference type.
   */
  public ReferenceTypeId(TypeTag typeTag, long id, ClassInfo classInfo) {
    super(id, classInfo);
    this.typeTag = typeTag;
  }

  public static ReferenceTypeId factory(long id, ClassInfo classInfo) {
    if (classInfo.isArray()) {
      return new ArrayTypeReferenceId(id, classInfo);
    }
    if (classInfo.isInterface()) {
      return new InterfaceTypeReferenceId(id, classInfo);
    }

    return new ClassTypeReferenceId(id, classInfo);
  }

  public static ReferenceTypeId factory(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
    return TypeTag.ARRAY.convert(bytes.get()).createReferenceTypeId(bytes, contextProvider);
    // TODO delete this if it is unused .. and also all other methods
  }

  @Override
  public IdentifiableEnum<Byte> getIdentifier() {
    return typeTag;
  }

  @Override
  public ClassInfo nullObjectHandler() throws InvalidIdentifier {
    throw new InvalidReferenceType(this);
  }

}
