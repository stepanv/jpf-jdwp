package gov.nasa.jpf.jdwp.event;

import java.io.DataOutputStream;
import java.io.IOException;

import gov.nasa.jpf.jdwp.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.vm.ThreadInfo;

public class VmInitStart extends Event implements Threadable {

	public VmInitStart(ThreadInfo currentThread) {
		super(EventKind.VM_START, (ThreadId) JdwpObjectManager.getInstance().getObjectId(currentThread));
	}

	@Override
	protected void writeSpecific(DataOutputStream os) throws IOException {
	}

}
