package gov.nasa.jpf.jdwp.event;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import gov.nasa.jpf.jdwp.JdwpObjectManager;
import gov.nasa.jpf.jdwp.event.filter.ClassMatchFilter;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.variable.StringRaw;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.ThreadInfo;

public class ClassPrepareEvent extends Event {

	private int status;
	private ClassInfo classInfo;

	public ClassPrepareEvent(ThreadInfo currentThread, ClassInfo classInfo, int status) {
		super(EventKind.CLASS_PREPARE, (ThreadId) JdwpObjectManager.getInstance().getObjectId(currentThread));
		
		this.classInfo = classInfo;
		this.status = status;
		
	}

	@Override
	protected void writeSpecific(DataOutputStream os) throws IOException {
		JdwpObjectManager.getInstance().getReferenceTypeId(classInfo).writeTagged(os);
		new StringRaw(classInfo.getSignature()).write(os);
		os.writeInt(status);
	}
	
	@Override
	public boolean matchesClassPattern(ClassMatchFilter classMatchFilter) {
		return classMatchFilter.matches(classInfo.getName());
	}

}
