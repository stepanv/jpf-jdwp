package gov.nasa.jpf.jdwp.id;

import gov.nasa.jpf.jdwp.exception.InvalidFrameId;
import gov.nasa.jpf.vm.StackFrame;

public class FrameId extends Identifier<StackFrame> {

  public FrameId(long id, StackFrame object) {
    super(id, object);
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
