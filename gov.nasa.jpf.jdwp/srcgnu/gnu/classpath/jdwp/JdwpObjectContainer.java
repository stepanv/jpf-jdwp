package gnu.classpath.jdwp;

import gnu.classpath.jdwp.id.ObjectId;
import gov.nasa.jpf.vm.ClassInfo;

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
		throw new RuntimeException("NOT IMPLEMENTED .. SHOULDN'T BE USED?");
//		return ClassInfo.getResolvedClassInfo(object.getClass().getCanonicalName());
	}

}
