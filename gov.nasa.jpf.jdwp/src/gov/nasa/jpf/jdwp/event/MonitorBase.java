package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.type.Location;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;

public class MonitorBase extends LocatableEvent {

	private ElementInfo taggedObject;

	public MonitorBase(EventKind eventKind, ThreadInfo threadInfo, ElementInfo taggedObject, Location location) {
		super(eventKind, threadInfo, location);
		this.taggedObject = taggedObject;
	}

	/**
	 * Overrides threadable specific write since we have to write tagged Object
	 * Id before it's actual location.
	 */
	@Override
	protected void writeThreadableSpecific(DataOutputStream os) throws IOException {
		ObjectId taggedObjectId = JdwpObjectManager.getInstance().getObjectId(taggedObject);
		taggedObjectId.write(os);
		getLocation().write(os);

	}

	@Override
	protected void writeLocatableSpecific(DataOutputStream os) throws IOException {
		//empty
	}

}
