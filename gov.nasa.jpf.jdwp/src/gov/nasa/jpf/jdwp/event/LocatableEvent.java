package gov.nasa.jpf.jdwp.event;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import gov.nasa.jpf.jdwp.Locatable;
import gov.nasa.jpf.jdwp.event.filter.ClassMatchFilter;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.type.Location;

public abstract class LocatableEvent extends Event implements Locatable {

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
	public boolean matchesClassPattern(ClassMatchFilter classMatchFilter) {
		String className = location.getInstruction().getMethodInfo().getClassName();
		return classMatchFilter.matches(className);
	}

	protected abstract void writeLocatableSpecific(DataOutputStream os)throws IOException;

}
