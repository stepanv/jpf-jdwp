package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class ClassLoaderId extends InfoObjectId<ClassLoaderInfo>  {

	public ClassLoaderId(long id, ClassLoaderInfo classLoaderInfo) {
		super(Tag.CLASS_LOADER, id, VM.getVM().getHeap().get(classLoaderInfo.getClassLoaderObjectRef()), classLoaderInfo);
	}

	public ClassLoaderId(long id, ElementInfo elementInfo) {
		this(id, getClassLoaderInfo(elementInfo));
	}

	/**
	 * @param elementInfo
	 * @return
	 */
	private static ClassLoaderInfo getClassLoaderInfo(ElementInfo elementInfo) {
		ThreadInfo currentThread = VM.getVM().getCurrentThread();
		MJIEnv env = currentThread.getMJIEnv();
		// TODO maybe don't use current thread but something better...
		
		return env.getClassLoaderInfo(elementInfo.getObjectRef());
	}
}
