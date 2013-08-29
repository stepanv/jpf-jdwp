package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.vm.ThreadInfo;

public interface Threadable extends Event {
  public ThreadInfo getThread();
}
