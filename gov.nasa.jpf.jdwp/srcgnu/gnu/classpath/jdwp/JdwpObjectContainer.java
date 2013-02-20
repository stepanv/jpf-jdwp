package gnu.classpath.jdwp;

import gnu.classpath.jdwp.id.ObjectId;
import gov.nasa.jpf.jvm.ClassInfo;

public abstract class JdwpObjectContainer<T, ID extends ObjectId> {
	
	private T object;

	protected JdwpObjectContainer(T object) {
		this.object = object;
	}
	
	public T getData() {
		return object;
	}

	abstract public ID createId();

	public ClassInfo getClassInfo() {
		return ClassInfo.getResolvedClassInfo(object.getClass().getCanonicalName());
	}

}
