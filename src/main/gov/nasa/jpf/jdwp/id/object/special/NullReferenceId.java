/**
 * 
 */
package gov.nasa.jpf.jdwp.id.object.special;

import gov.nasa.jpf.jdwp.command.ClassTypeCommand;
import gov.nasa.jpf.jdwp.command.IdentifiableEnum;
import gov.nasa.jpf.jdwp.exception.id.object.NullPointerObjectException;
import gov.nasa.jpf.jdwp.id.IdentifierBase;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.vm.ClassInfo;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * The null representation of reference ID that is <i>zero</i>.<br/>
 * Note that this class has only very limited usage and a purpose such as in
 * {@link ClassTypeCommand#SUPERCLASS} command
 * 
 * @author stepan
 * 
 */
public class NullReferenceId extends IdentifierBase<ClassInfo> implements ReferenceTypeId {

  private static final NullReferenceId instance = new NullReferenceId();

  /**
   * The private constructor - this is a singleton only class.
   */
  private NullReferenceId() {
    super(NULL_IDENTIFIER_ID, null);
  }

  /**
   * Get the instance of this singleton.
   * 
   * @return The {@link NullReferenceId} null representation of reference ID.
   * @see NullReferenceId
   */
  public static NullReferenceId getInstance() {
    return instance;
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nasa.jpf.jdwp.id.TaggableIdentifier#getIdentifier()
   */
  @Override
  public IdentifiableEnum<Byte> getIdentifier() {
    throw new NullPointerException();
  }

  /**
   * Tagged write is not supported since the specification doesn't mention what
   * tag should be used in a such case.
   */
  @Override
  public void writeTagged(DataOutputStream os) throws IOException {
    throw new NullPointerException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nasa.jpf.jdwp.id.Identifier#nullObjectHandler()
   */
  @Override
  public ClassInfo nullObjectHandler() throws NullPointerObjectException {
    throw new NullPointerObjectException();
  }

  /**
   * Act as a array type too.
   */
  @Override
  public boolean isArrayType() {
    return true;
  }

  /**
   * Act as a class type too.
   */
  @Override
  public boolean isClassType() {
    return true;
  }

  /**
   * Act as a interface type too.
   */
  @Override
  public boolean isInterfaceType() {
    return true;
  }
  
  @Override
  public String toString() {
    return "NullReferenceId: ID: 0";
  }

}
