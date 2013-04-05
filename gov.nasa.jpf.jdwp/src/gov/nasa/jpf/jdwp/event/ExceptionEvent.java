package gov.nasa.jpf.jdwp.event;

import java.io.DataOutputStream;
import java.io.IOException;

import gov.nasa.jpf.jdwp.JdwpObjectManager;
import gov.nasa.jpf.jdwp.event.filter.ExceptionOnlyFilter;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.jvm.ClassInfo;

public class ExceptionEvent extends LocatableEvent implements ExceptionOnlyFilterable, LocationOnlyFilterable  {

	
	
	private ClassInfo exception;
	private Location catchLocation;

	public ExceptionEvent(ThreadId threadId, Location location, ClassInfo exception, Location catchLocation) {
		super(EventKind.EXCEPTION, threadId, location);
		
		this.exception = exception;
		this.catchLocation = catchLocation;
	}

	@Override
	protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
		JdwpObjectManager.getInstance().getObjectId(exception).writeTagged(os);
		catchLocation.write(os);
	}

	@Override
	public boolean visit(ExceptionOnlyFilter exceptionOnlyFilter) {
		throw new RuntimeException("NOT IMPLEMENTED YET");
	}

}
