package gov.nasa.jpf.jdwp;

import java.io.DataOutputStream;
import java.io.IOException;

public enum ClassStatus {
	VERIFIED(1), PREPARED(2), INITIALIZED(4), ERROR(8);
	
	private int statusId;

	ClassStatus(int statusId) {
		this.statusId = statusId;
	}
	
	public void write(DataOutputStream os) throws IOException {
		os.writeInt(statusId);
	}
}
