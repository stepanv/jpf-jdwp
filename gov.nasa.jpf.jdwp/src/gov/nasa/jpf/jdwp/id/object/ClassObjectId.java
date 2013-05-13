package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.VM;

public class ClassObjectId extends InfoObjectId<ClassInfo> {

	public ClassObjectId(long id, ClassInfo classInfo) {
		super(Tag.CLASS_OBJECT, id, classInfo.getClassObject(), classInfo);
	}
	
	public ClassObjectId(long id, ElementInfo elementInfo) {
		this(id, getClassInfo(elementInfo));
	}

	private static ClassInfo getClassInfo(ElementInfo elementInfo) {
		int typeNameRef = elementInfo.getReferenceField("name");
	    ElementInfo typeName = VM.getVM().getHeap().get(typeNameRef);
	    String reflectedTypeString = typeName.asString();
	    ClassInfo ci = ClassInfo.getInitializedClassInfo(reflectedTypeString, VM.getVM().getCurrentThread());
	    return ci;
	}
	
}
