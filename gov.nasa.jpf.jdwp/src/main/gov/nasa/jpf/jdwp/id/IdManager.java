package gov.nasa.jpf.jdwp.id;

import gov.nasa.jpf.jdwp.exception.InvalidIdentifier;
import gov.nasa.jpf.vm.ElementInfo;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdManager<I extends Identifier<T>, T> {

	final static Logger logger = LoggerFactory.getLogger(IdManager.class);

	public interface IdFactory<I, T> {
		I create(long id, T object);
	}

	// TODO when an identifier doesn't contain mapping to an object it should
	// remove itself from here (to prevent memory leaking)
	private Map<Long, I> idToIdentifierMap = new HashMap<Long, I>();
	private Map<T, I> objectToIdentifierMap = new HashMap<T, I>();
	private Long idGenerator = (long) 1;
	private IdFactory<I, T> idFactory;

	public IdManager(IdFactory<I, T> idFactory) {
		this.idFactory = idFactory;
	}

	public synchronized I getIdentifierId(T object) {
		// TODO if object == null we should probably return NullObjectId (which
		// is incompatible with ObjectId children which is BAD)
		if (object == null) {
			throw new RuntimeException("Null is not allowed here!");
		}
		if (objectToIdentifierMap.containsKey(object)) {
			// System.out.println("ALREADY EXISTS: " +
			// objectToIdentifierMap.get(object).toString() + " object:" +
			// object + " class:" + object.getClass());
			I identifier = objectToIdentifierMap.get(object);
			try {
				/*
				 * This section is just for debugging purposes TODO delete this
				 */
				T alternateObject = identifier.get();
				if (!alternateObject.equals(object)) {
					logger.error("A BIG PROBLEM!");
					logger.error("BIG PROBLEM: " + object + " maps to: " + alternateObject);

					logger.error("Object hash code: " + object.hashCode());
					logger.error("Alternate hash code: " + alternateObject.hashCode());

					HashMap<T, I> testMap = new HashMap<T, I>();
					testMap.put(alternateObject, identifier);
					if (testMap.containsKey(object)) {
						logger.error("WEIRD FOR THE SECOND TIME!");
					}

					Map<T, I> testWeakMap = new WeakHashMap<T, I>();
					testWeakMap.put(alternateObject, identifier);
					if (testWeakMap.containsKey(object)) {
						logger.error("WEIRD FOR THE THIRD TIME!");
					}
					logger.error("{}", objectToIdentifierMap.get(object));

					if (alternateObject instanceof ElementInfo && object instanceof ElementInfo) {
						if (((ElementInfo) alternateObject).getObjectRef() == ((ElementInfo) object).getObjectRef()) {
							// I'm so not sure what to do here ... TODO .. is
							// this ok or not?
							return identifier;
						}
					}

					throw new RuntimeException("BIG PROBLEM: " + object + " maps to: " + alternateObject);
				}
			} catch (InvalidIdentifier e) {
				// alternateObject is used just for debugging purposes
			}
			return identifier;
		} else {
			Long id = idGenerator++;
			I identifier = idFactory.create(id, object);
			objectToIdentifierMap.put(object, identifier);
			idToIdentifierMap.put(id, identifier);

			if (logger.isDebugEnabled()) {
				if (object instanceof ElementInfo) {
					logger.debug("Created ID: {}, (identifier: {}) object: {}, class: {}, classInfo: {}", id, identifier, object, object.getClass(),
							((ElementInfo) object).getClassInfo());
				} else {
					logger.debug("Created ID: {}, (identifier: {}) object: {}, class: {}", id, identifier, object, object.getClass());
				}
			}

			// try {
			// if (identifier.get() != object) {
			// System.out.println("WTF?");
			// I identifier2 = idFactory.create(id, object);
			// if (identifier2.get() != object) {
			// System.out.println("WTF 2?");
			// }
			//
			// }
			// } catch (InvalidObject e) {
			// }

			return identifier;
		}
	}

	public synchronized I readIdentifier(ByteBuffer bytes) {
		// TODO throw ErrorType.INVALID_OBJECT
		return idToIdentifierMap.get(bytes.getLong());
	}
}
