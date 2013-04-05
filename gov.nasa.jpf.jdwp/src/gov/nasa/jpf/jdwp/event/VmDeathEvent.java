package gov.nasa.jpf.jdwp.event;

import java.io.DataOutputStream;
import java.io.IOException;

public class VmDeathEvent extends Event {

	public VmDeathEvent() {
		super(EventKind.VM_DEATH, null);
	}

	@Override
	protected void writeSpecific(DataOutputStream os) throws IOException {
	}
	
	@Override
	public void write(DataOutputStream os, int requestId) throws IOException {
		os.writeInt(requestId);
	}

}
