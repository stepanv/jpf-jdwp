package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.id.TaggableIdentifier;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class ObjectId<T> extends TaggableIdentifier<T> implements Value {
	
	private Tag tag;

	public ObjectId(Tag tag, long id, T object) {
		super(id, object);
		this.tag = tag;
	}

	public static ObjectId<?> factory(long id, Object object) {
		if (object instanceof ElementInfo && ((ElementInfo)object).getClassInfo().isArray()) {
	        return new ArrayId(id, (ElementInfo) object);
	    } else if (object.getClass().getName().equals("gov.nasa.jpf.vm.ThreadInfo")) { // TODO don't use string comparison - it's slow
	    	return new ThreadId(id, (ThreadInfo) object);
	    } else if (object instanceof ElementInfo && ((ElementInfo)object).getClassInfo().isStringClassInfo()) {
	    	return new StringId(id, (ElementInfo) object);
	    } else if (object instanceof ElementInfo && ((ElementInfo)object).getClassInfo().getName().equals("java.lang.Class")) {
	    	return new ClassObjectId(id, (ElementInfo) object);
	    } else if (object instanceof ElementInfo && ((ElementInfo)object).getClassInfo().getName().equals("java.lang.ThreadGroup")) {
	    	return new ThreadGroupId(id, (ElementInfo) object);
	    } else if (object instanceof ElementInfo && ((ElementInfo)object).getClassInfo().getName().equals("java.lang.ClassLoader")) {
	    	return new ClassLoaderId(id, (ElementInfo) object);
	    } else {
	    	return new ObjectId<Object>(Tag.OBJECT, id, object);
	    }
		
		//throw new RuntimeException("FACTORY NOT FULLY IMPLEMENTED YET. For object: " + object + " class: " + object.getClass());
	}

	@Override
	public Tag getIdentifier() {
		return tag;
	}

	@Override
	public void push(StackFrame frame) throws InvalidObject {
		int ref = ((ElementInfo)this.get()).getObjectRef();
		frame.pushRef(ref);
	}
}
