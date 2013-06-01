package gov.nasa.jpf.jdwp.id;


import gov.nasa.jpf.vm.ElementInfo;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class IdManager<I extends Identifier<T>, T> {
	
	public interface IdFactory<I, T> {
		I create(long id, T object);
	}
	
	// TODO when an identifier doesn't contain mapping to an object it should remove itself from here (to prevent memory leaking)
	private Map<Long, I> idToIdentifierMap = new HashMap<Long, I>();
	private Map<T, I> objectToIdentifierMap = new WeakHashMap<T, I>();
	private Long idGenerator = (long) 1;
	private IdFactory<I, T> idFactory;

	public IdManager(IdFactory<I, T> idFactory) {
		this.idFactory = idFactory;
	}

	public void setIdFactory(IdFactory<I, T> idFactory) {
		this.idFactory = idFactory;
	}

	public synchronized I getIdentifierId(T object) {
		// TODO if object == null we should probably return NullObjectId (which is incompatible with ObjectId children which is BAD)
		if (object == null) {
			throw new RuntimeException("Null is not allowed here!");
		}
		if (objectToIdentifierMap.containsKey(object)) {
			//System.out.println("ALREADY EXISTS: " + objectToIdentifierMap.get(object).toString() + " object:" + object + " class:" + object.getClass());
			return objectToIdentifierMap.get(object);
		} else {
			Long id = idGenerator++;
			I identifier = idFactory.create(id, object);
			objectToIdentifierMap.put(object, identifier);
			idToIdentifierMap.put(id, identifier);
			
			if (object instanceof ElementInfo) {
				System.out.println("CREATED OBJECT id: " + id + " (identifier: " + identifier + ") object:" + object + " class:" + object.getClass() + " classInfo: " + ((ElementInfo)object).getClassInfo());
			} else {
				System.out.println("CREATED OBJECT id: " + id + " (identifier: " + identifier + ") object:" + object + " class:" + object.getClass());
			}
			
			return identifier;
		}
	}

	public I readIdentifier(ByteBuffer bytes) {
		// TODO throw ErrorType.INVALID_OBJECT
		return idToIdentifierMap.get(bytes.getLong());
	}
}
