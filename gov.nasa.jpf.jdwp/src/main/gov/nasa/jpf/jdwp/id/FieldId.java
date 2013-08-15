package gov.nasa.jpf.jdwp.id;

import gov.nasa.jpf.jdwp.exception.InvalidFieldId;
import gov.nasa.jpf.jdwp.exception.InvalidIdentifier;
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
public class FieldId extends Identifier<FieldInfo> {

	/**
	 * Field ID constructor.
	 * 
	 * @param id
	 *            The numerical ID of this identifier.
	 * @param fieldInfo
	 *            The {@link FieldInfo} this identifier is created for.
	 */
	public FieldId(Long id, FieldInfo fieldInfo) {
		super(id, fieldInfo);
	}

	@Override
	public FieldInfo nullObjectHandler() throws InvalidIdentifier {
		throw new InvalidFieldId(this);
	}

}
