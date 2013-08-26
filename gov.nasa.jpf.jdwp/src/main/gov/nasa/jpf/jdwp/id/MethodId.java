package gov.nasa.jpf.jdwp.id;

import gov.nasa.jpf.jdwp.exception.InvalidIdentifier;
import gov.nasa.jpf.jdwp.exception.InvalidMethodId;
import gov.nasa.jpf.vm.MethodInfo;

/**
 * TODO use MethodIds properly as other Identifiers are used, i.e. Don't rely on
 * global ids for methods.
 * 
 * @author stepan
 * 
 */
public class MethodId extends Identifier<MethodInfo> {

  public MethodId(long globalMethodId) {
    super(0, MethodInfo.getMethodInfo((int) globalMethodId));
  }

  @Override
  public MethodInfo nullObjectHandler() throws InvalidIdentifier {
    throw new InvalidMethodId(this);
  }

}
