/**
 * 
 */
package gov.nasa.jpf.jdwp.id.object.special;

import gov.nasa.jpf.jdwp.command.IdentifiableEnum;
import gov.nasa.jpf.jdwp.exception.id.InvalidIdentifierException;
import gov.nasa.jpf.jdwp.id.IdentifierBase;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.vm.ClassInfo;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author stepan
 *
 */
public class NullReferenceId extends IdentifierBase<ClassInfo> implements ReferenceTypeId {

  private static final NullReferenceId instance = new NullReferenceId();
  
  private NullReferenceId() {
    super(NULL_IDENTIFIER_ID, null);
  }

  public static NullReferenceId getInstance() {
    return instance;
  }

  /* (non-Javadoc)
   * @see gov.nasa.jpf.jdwp.id.TaggableIdentifier#getIdentifier()
   */
  @Override
  public IdentifiableEnum<Byte> getIdentifier() {
    throw new NullPointerException();
  }

  /* (non-Javadoc)
   * @see gov.nasa.jpf.jdwp.id.TaggableIdentifier#writeTagged(java.io.DataOutputStream)
   */
  @Override
  public void writeTagged(DataOutputStream os) throws IOException {
    throw new NullPointerException();
  }

  /* (non-Javadoc)
   * @see gov.nasa.jpf.jdwp.id.Identifier#nullObjectHandler()
   */
  @Override
  public ClassInfo nullObjectHandler() throws InvalidIdentifierException {
    return null;
  }

  /* (non-Javadoc)
   * @see gov.nasa.jpf.jdwp.id.type.ReferenceTypeId#isArrayType()
   */
  @Override
  public boolean isArrayType() {
    return true;
  }

  /* (non-Javadoc)
   * @see gov.nasa.jpf.jdwp.id.type.ReferenceTypeId#isClassType()
   */
  @Override
  public boolean isClassType() {
    return true;
  }

  /* (non-Javadoc)
   * @see gov.nasa.jpf.jdwp.id.type.ReferenceTypeId#isInterfaceType()
   */
  @Override
  public boolean isInterfaceType() {
    return true;
  }

}
