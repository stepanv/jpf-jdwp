package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.event.filter.ClassFilter;
import gov.nasa.jpf.jdwp.event.filter.ClassOnlyFilter;
import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.vm.ClassInfo;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class LocatableEvent extends EventBase implements Locatable {

	private Location location;

	public LocatableEvent(EventKind eventKind, ThreadId threadId, Location location) {
		super(eventKind, threadId);
		
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}
	
	@Override
	protected void writeSpecific(DataOutputStream os) throws IOException {
		location.write(os);
		writeLocatableSpecific(os);
		
	}
	
	@Override
	public boolean matches(ClassFilter classMatchFilter) {
		String className = location.getInstruction().getMethodInfo().getClassName();
		return classMatchFilter.matches(className);
	}

	@Override
	public boolean matches(ClassOnlyFilter classOnlyFilter) throws InvalidObject {
		ClassInfo classInfo = location.getInstruction().getMethodInfo().getClassInfo();
		return classOnlyFilter.matches(classInfo);
	}

	protected abstract void writeLocatableSpecific(DataOutputStream os)throws IOException;

}
