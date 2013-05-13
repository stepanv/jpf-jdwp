package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.InfoObject;

import java.lang.ref.WeakReference;

public abstract class InfoObjectId<T extends InfoObject> extends ObjectId {

	public InfoObjectId(Tag tag, long id, ElementInfo object, T infoObject) {
		super(tag, id, object);
		
		infoObjectReference = new WeakReference<T>(infoObject);
	}
	
	private WeakReference<T> infoObjectReference;
	
	public T getInfoObject() throws InvalidObject {
		T infoObject = infoObjectReference.get();
		if (infoObject == null) {
			throw new InvalidObject("ObjectId: " + this);
		}
		return infoObject;
	}

}
