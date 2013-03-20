package gov.nasa.jpf.jdwp.id.type;

import gov.nasa.jpf.jdwp.command.IdentifiableEnum;
import gov.nasa.jpf.jdwp.id.TaggableIdentifier;
import gov.nasa.jpf.jvm.ClassInfo;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 
 * <strong>JDWP Specification:</strong>
 * <p>
 * Uniquely identifies a reference type in the target VM. It should not be
 * assumed that for a particular class, the <tt>classObjectID</tt> and the
 * <tt>referenceTypeID</tt> are the same. A particular reference type will be identified
 * by exactly one ID in JDWP commands and replies throughout its lifetime A
 * referenceTypeID is not reused to identify a different reference type,
 * regardless of whether the referenced class has been unloaded.
 * </p>
 * 
 * @author stepan
 * 
 * @param <T>
 */
public class ReferenceTypeId extends TaggableIdentifier<ClassInfo> {
	
	public enum TypeTag implements IdentifiableEnum<Byte>{
		/** ReferenceType is a class. */
		CLASS(1)  ,
		/** ReferenceType is an interface. */
		INTERFACE(2),
		/** ReferenceType is an array. */
		ARRAY(3);	
		
		private byte typeTagId;

		TypeTag(int typeTagId) {
			this.typeTagId = (byte)typeTagId;
		}

		@Override
		public Byte identifier() {
			return typeTagId;
		}

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
	
	@Override
	public IdentifiableEnum<Byte> getIdentifier() {
		return typeTag;
	}

}
