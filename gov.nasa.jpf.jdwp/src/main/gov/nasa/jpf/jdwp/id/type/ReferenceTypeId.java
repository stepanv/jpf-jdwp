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
 * 
 * <strong>JDWP Specification:</strong>
 * <p>
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
