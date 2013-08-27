package gov.nasa.jpf.jdwp.id;

import gov.nasa.jpf.jdwp.exception.InvalidIdentifier;
import gov.nasa.jpf.jdwp.exception.InvalidMethodId;
import gov.nasa.jpf.vm.MethodInfo;

/**
 * This class implements the corresponding <code>methodID</code> common data
 * type from the JDWP Specification.
 * 
 * <p>
 * <h2>JDWP Specification</h2>
 * Uniquely identifies a method in some class in the target VM. The methodID
 * must uniquely identify the method within its class/interface or any of its
 * subclasses/subinterfaces/implementors. A methodID is not necessarily unique
 * on its own; it is always paired with a referenceTypeID to uniquely identify
 * one method. The referenceTypeID can identify either the declaring type of the
 * method or a subtype.
 * </p>
 * 
 * TODO use MethodIds properly as other Identifiers are used, i.e. Don't rely on
 * global ids for methods.
 * 
 * @author stepan
 * 
 */
public class MethodId extends Identifier<MethodInfo> {

	/**
	 * Method ID constructor.
	 * 
	 * @param id
	 *            The numerical ID of this identifier.
	 */
  public MethodId(long globalMethodId) {
    super(0, MethodInfo.getMethodInfo((int) globalMethodId));
  }

  @Override
  public MethodInfo nullObjectHandler() throws InvalidIdentifier {
    throw new InvalidMethodId(this);
  }

}
