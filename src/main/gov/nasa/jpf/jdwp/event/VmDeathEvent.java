package gov.nasa.jpf.jdwp.event;

import java.io.DataOutputStream;
import java.io.IOException;

public class VmDeathEvent extends EventBase {

  public VmDeathEvent() {
    super(EventKind.VM_DEATH);
  }

  @Override
  protected void writeSpecific(DataOutputStream os) throws IOException {
    // this body is empty
  }

}
