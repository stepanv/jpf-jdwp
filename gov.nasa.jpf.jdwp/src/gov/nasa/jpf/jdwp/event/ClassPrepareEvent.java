package gov.nasa.jpf.jdwp.event;

import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.ThreadInfo;

public class ClassPrepareEvent extends Event {

	public ClassPrepareEvent(int requestId, ThreadInfo currentThread, ClassInfo classInfo, int i) {
		super(requestId, EventKind.CLASS_PREPARE);
		
		// TODO Auto-generated constructor stub
	}

}
