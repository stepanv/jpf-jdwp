package gov.nasa.jpf.jdwp;

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.FieldId;
import gov.nasa.jpf.jdwp.id.MethodId;
import gov.nasa.jpf.jdwp.id.object.ArrayId;
import gov.nasa.jpf.jdwp.id.object.ClassObjectId;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.jdwp.id.object.special.NullObjectId;
import gov.nasa.jpf.jdwp.id.type.ArrayTypeReferenceId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class JdwpObjectManager {

	private static class JdwpObjectManagerHolder {
		private static final JdwpObjectManager instance = new JdwpObjectManager();
	}

	public static JdwpObjectManager getInstance() {
		return JdwpObjectManagerHolder.instance;
	}

	private JdwpObjectManager() {
		// An objectID of 0 represents a null object.
		idObjectMap.put((long) 0, NullObjectId.getInstance());
	}

	public ThreadId readThreadId(ByteBuffer bytes) throws JdwpError {
		long id = bytes.getLong();

		synchronized (idObjectMap) {
			if (!idObjectMap.containsKey(id)) {
				System.err.println("Invalid id: " + id);
				throw new JdwpError(ErrorType.INVALID_THREAD);
			}
			return (ThreadId) idObjectMap.get(id);
		}

	}

	public ObjectId readObjectId(ByteBuffer bytes) throws JdwpError {
		long id = bytes.getLong();

		synchronized (idObjectMap) {
			if (!idObjectMap.containsKey(id)) {
				System.err.println("Invalid id: " + id);
				throw new JdwpError(ErrorType.INVALID_OBJECT);
			}
			return (ObjectId) idObjectMap.get(id);
		}

	}

	public ThreadId readSafeThreadId(ByteBuffer bytes) throws JdwpError {
		return (ThreadId) readSafeObjectId(bytes);
	}

	public ArrayId readArrayId(ByteBuffer bytes) throws JdwpError {
		return (ArrayId) readSafeObjectId(bytes);
	}

	public ObjectId readSafeObjectId(ByteBuffer bytes) throws JdwpError {
		long id = bytes.getLong();

		synchronized (idObjectMap) {
			if (!idObjectMap.containsKey(id)) {
				System.err.println("Invalid id: " + id);
				throw new JdwpError(ErrorType.INVALID_OBJECT);
			}
			return (ObjectId) idObjectMap.get(id);
		}

	}

	public ArrayTypeReferenceId readArrayTypeReferenceId(ByteBuffer bytes) throws JdwpError {
		return (ArrayTypeReferenceId) readReferenceTypeId(bytes);
	}

	public ReferenceTypeId readReferenceTypeId(ByteBuffer bytes) throws JdwpError {
		long id = bytes.getLong();

		synchronized (idReferenceTypeMap) {
			if (!idReferenceTypeMap.containsKey(id)) {
				System.err.println("Invalid id: " + id);
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
		System.out.println("CREATED REF TYPE id: " + id + " classinfo:" + classInfo);
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

	public ObjectId getObjectId(ElementInfo object) {

		if (object == null) {
			return NullObjectId.getInstance();
		}
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

	private long generateId() {
		synchronized (objectIdGenerator) {
			return objectIdGenerator++;
		}
	}

	private ObjectId createObjectId(ElementInfo elementInfo) {
		long id = generateId();

		ObjectId objectId = ObjectId.factory(id, elementInfo);
		idObjectMap.put(id, objectId);

		System.out.println("CREATED OBJECT id: " + id + " object:" + elementInfo + " class:" + elementInfo.getClass());

		return objectId;
	}

	Map<Long, FieldId> idField = new WeakHashMap<Long, FieldId>();
	Map<FieldInfo, FieldId> fieldInfoFieldId = new WeakHashMap<FieldInfo, FieldId>();
	Long fieldIdGenerator = (long) 1;

	public FieldId getFieldId(FieldInfo field) {
		synchronized (fieldInfoFieldId) {
			if (fieldInfoFieldId.containsKey(field)) {
				return fieldInfoFieldId.get(field);
			} else {
				long id = fieldIdGenerator++;
				FieldId fieldId = new FieldId(id, field);
				fieldInfoFieldId.put(field, fieldId);
				synchronized (idField) {
					idField.put(id, fieldId);
				}
				return fieldId;
			}
		}
	}

	public FieldId readFieldId(ByteBuffer bytes) {
		synchronized (idField) {
			return idField.get(bytes.getLong());
		}
	}

	public ClassObjectId readClassObjectId(ByteBuffer bytes) throws JdwpError {
		return (ClassObjectId) readSafeObjectId(bytes);
	}

	public ClassObjectId getClassObjectId(ClassInfo classInfo) {
		if (classInfo == null) {
			throw new RuntimeException("Null is not allowed here!");
		}
		synchronized (objectIdMap) {
			ClassObjectId classObjectId = (ClassObjectId) objectIdMap.get(classInfo.getClassObject());

			if (classObjectId != null) {
				return classObjectId;
			}

			long id = generateId();
			classObjectId = new ClassObjectId(id, classInfo);
			idObjectMap.put(id, classObjectId);
			objectIdMap.put(classInfo.getClassObject(), classObjectId);
			return classObjectId;
		}
	}

	public ThreadId getThreadId(ThreadInfo threadInfo) {
		if (threadInfo == null) {
			throw new RuntimeException("Null is not allowed here!");
		}
		synchronized (objectIdMap) {
			ThreadId threadId = (ThreadId) objectIdMap.get(threadInfo.getThreadObject());

			if (threadId != null) {
				return threadId;
			}

			long id = generateId();
			threadId = new ThreadId(id, threadInfo);
			idObjectMap.put(id, threadId);
			objectIdMap.put(threadInfo.getThreadObject(), threadId);
			return threadId;
		}
	}

}
