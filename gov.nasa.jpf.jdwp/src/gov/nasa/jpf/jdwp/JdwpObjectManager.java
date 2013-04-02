package gov.nasa.jpf.jdwp;

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jvm.ClassInfo;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JdwpObjectManager {
	
	private static class JdwpObjectManagerHolder {
		private static final JdwpObjectManager instance = new JdwpObjectManager();
	}
	
	public static JdwpObjectManager getInstance() {
		return JdwpObjectManagerHolder.instance;
	}
	
	public ThreadId readThreadId(ByteBuffer bytes) throws JdwpError {
		long id = bytes.getLong();
		
		synchronized (idObjectMap) {
			if (!idObjectMap.containsKey(id)) {
				throw new JdwpError(ErrorType.INVALID_THREAD);
			}
			return (ThreadId) idObjectMap.get(id);
		}
		
	}
	public ObjectId readObjectId(ByteBuffer bytes) throws JdwpError {
		long id = bytes.getLong();
		
		synchronized (idObjectMap) {
			if (!idObjectMap.containsKey(id)) {
				throw new JdwpError(ErrorType.INVALID_OBJECT);
			}
			return (ObjectId) idObjectMap.get(id);
		}
		
	}
	
	public ReferenceTypeId readReferenceTypeId(ByteBuffer bytes) throws JdwpError {
		long id = bytes.getLong();
		
		synchronized (idReferenceTypeMap) {
			if (!idReferenceTypeMap.containsKey(id)) {
				throw new JdwpError(ErrorType.INVALID_CLASS);
			}
			return idReferenceTypeMap.get(id);
		}
		
	}

	private Long referenceIdGenerator = (long) 1;
	private Long objectIdGenerator = (long) 1;
	
	private ReferenceTypeId createReferenceTypeId(ClassInfo classInfo) {
		long id;
		synchronized (referenceIdGenerator) {
			id = referenceIdGenerator++;
		}
		ReferenceTypeId referenceTypeId = ReferenceTypeId.factory(id, classInfo);
		idReferenceTypeMap.put(id, referenceTypeId);
		return referenceTypeId;
	}
	
	Map<ClassInfo, ReferenceTypeId> referenceTypeIdMap = new ConcurrentHashMap<ClassInfo, ReferenceTypeId>();
	Map<Long, ReferenceTypeId> idReferenceTypeMap = new ConcurrentHashMap<Long, ReferenceTypeId>();
	
	Map<Object, ObjectId> objectIdMap = new ConcurrentHashMap<Object, ObjectId>();
	Map<Long, ObjectId> idObjectMap = new ConcurrentHashMap<Long, ObjectId>();
	
	public ReferenceTypeId getReferenceTypeId(ClassInfo classInfo) {
		synchronized (referenceTypeIdMap) {
			ReferenceTypeId referenceTypeId = referenceTypeIdMap.get(classInfo);
			
			if (referenceTypeId != null) {
				return referenceTypeId;
			}
			
			referenceTypeId = createReferenceTypeId(classInfo);
			referenceTypeIdMap.put(classInfo, referenceTypeId);
			return referenceTypeId;
		}
	}
	
	public ObjectId getObjectId(Object object) {
		synchronized (objectIdMap) {
			ObjectId objectId = objectIdMap.get(object);
			
			if (objectId != null) {
				return objectId;
			}
			
			objectId = createObjectId(object);
			objectIdMap.put(object, objectId);
			return objectId;
		}
	}

	private ObjectId createObjectId(Object object) {
		long id;
		synchronized (objectIdGenerator) {
			id = objectIdGenerator++;
		}
		
		ObjectId objectId = ObjectId.factory(id, object);
		idObjectMap.put(id, objectId);
		
		return objectId;
	}
}
