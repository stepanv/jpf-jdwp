package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.id.TaggableIdentifier;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Fields;
import gov.nasa.jpf.vm.StackFrame;

public class ObjectId extends TaggableIdentifier<ElementInfo> implements Value {
	
	private Tag tag;

	public ObjectId(Tag tag, long id, ElementInfo object) {
		super(id, object);
		this.tag = tag;
	}

	public static ObjectId factory(long id, ElementInfo object) {
		ClassInfo classInfo = object.getClassInfo();
		if (classInfo.isArray()) {
	        return new ArrayId(id, object);
	    } else if ("java.lang.Thread".equals(classInfo.getName())) {
	    	return new ThreadId(id, object);
	    } else if (classInfo.isStringClassInfo()) {
	    	return new StringId(id, object);
	    } else if ("java.lang.Class".equals(classInfo.getName())) {
	    	return new ClassObjectId(id,object);
	    } else if ("java.lang.ThreadGroup".equals(classInfo.getName())) {
	    	return new ThreadGroupId(id, object);
	    } else if ("java.lang.ClassLoader".equals(classInfo.getName())) {
	    	return new ClassLoaderId(id, object);
	    } else {
	    	return new ObjectId(Tag.OBJECT, id, object);
	    }
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

	@Override
	public void modify(StackFrame stackFrame, int slotIndex) throws InvalidObject {
		int ref = ((ElementInfo)this.get()).getObjectRef();
		stackFrame.setLocalVariable(slotIndex, ref, true);
		
	}

	@Override
	public void modify(Fields fields, int index) throws InvalidObject {
		int ref = get().getObjectRef();
		fields.setReferenceValue(index, ref);
	}
}
