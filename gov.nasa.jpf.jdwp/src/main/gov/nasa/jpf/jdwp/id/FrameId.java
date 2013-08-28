package gov.nasa.jpf.jdwp.id;

import gov.nasa.jpf.jdwp.exception.InvalidFrameId;
import gov.nasa.jpf.vm.StackFrame;

/**
 * This class implements the corresponding <code>frameID</code> common data type
 * from the JDWP Specification.
 * 
 * <p>
 * <h2>JDWP Specification</h2>
 * Uniquely identifies a frame in the target VM. The frameID must uniquely
 * identify the frame within the entire VM (not only within a given thread). The
 * frameID need only be valid during the time its thread is suspended.
 * </p>
 * 
 * @author stepan
 * 
 */
public class FrameId extends Identifier<StackFrame> {

  /**
   * Frame ID constructor.
   * 
   * @param id
   *          The numerical ID of this identifier.
   * @param stackFrame
   *          The {@link StackFrame} this identifier is created for.
   */
  public FrameId(long id, StackFrame stackFrame) {
    super(id, stackFrame);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nasa.jpf.jdwp.id.Identifier#nullObjectHandler(gov.nasa.jpf.jdwp.id.
   * Identifier)
   */
  @Override
  public StackFrame nullObjectHandler() throws InvalidFrameId {
    throw new InvalidFrameId(this);
  }

}
