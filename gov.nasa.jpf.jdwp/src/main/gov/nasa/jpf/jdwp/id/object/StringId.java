package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.value.JdwpString;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.vm.ElementInfo;

/**
 * This class implements the corresponding stringID common data type from the
 * JDWP Specification.
 * 
 * <p>
 * <h2>JDWP Specification</h2>
 * Uniquely identifies an object in the target VM that is known to be a string
 * object. Note: this is very different from {@link JdwpString}, which is a
 * {@link Value}.
 * </p>
 * 
 * @author stepan
 * 
 */
public class StringId extends ObjectId {

  /**
   * Constructs the string ID.
   * 
   * @param id
   *          The ID known by {@link ObjectIdManager}
   * @param object
   *          The {@link ElementInfo} instance that needs JDWP ID
   *          representation.
   */
  public StringId(long id, ElementInfo object) {
    super(Tag.STRING, id, object);
  }

}
