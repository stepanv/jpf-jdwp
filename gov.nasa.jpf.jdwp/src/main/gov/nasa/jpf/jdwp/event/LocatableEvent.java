package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.event.filter.ClassFilter;
import gov.nasa.jpf.jdwp.event.filter.ClassOnlyFilter;
import gov.nasa.jpf.jdwp.exception.InvalidIdentifier;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class LocatableEvent extends ThreadableEvent implements Locatable {

  private Location location;

  public LocatableEvent(EventKind eventKind, ThreadInfo threadInfo, Location location) {
    super(eventKind, threadInfo);

    this.location = location;
  }

  public Location getLocation() {
    return location;
  }

  @Override
  protected void writeThreadableSpecific(DataOutputStream os) throws IOException {
    location.write(os);
    writeLocatableSpecific(os);
  }

  @Override
  public boolean matches(ClassFilter classMatchFilter) {
    String className = location.getInstruction().getMethodInfo().getClassName();
    return classMatchFilter.matches(className);
  }

  @Override
  public boolean matches(ClassOnlyFilter classOnlyFilter) throws InvalidIdentifier {
    ClassInfo classInfo = location.getInstruction().getMethodInfo().getClassInfo();
    return classOnlyFilter.matches(classInfo);
  }

  protected abstract void writeLocatableSpecific(DataOutputStream os) throws IOException;

  @Override
  public String toString() {
    return super.toString() + ", location: " + location;
  }

}
