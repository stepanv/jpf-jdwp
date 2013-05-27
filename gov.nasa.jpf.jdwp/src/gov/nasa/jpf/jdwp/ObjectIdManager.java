package gov.nasa.jpf.jdwp;

import gov.nasa.jpf.jdwp.id.object.ClassLoaderId;
import gov.nasa.jpf.jdwp.id.object.ClassObjectId;
import gov.nasa.jpf.jdwp.id.object.InfoObjectId;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.object.ThreadId;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import java.nio.ByteBuffer;

public class ObjectIdManager extends IdManager<ObjectId, ElementInfo> {

	public ObjectIdManager() {
		super(null);
	}

	public abstract class MorfableIdFactory<T> implements IdFactory<ObjectId, ElementInfo> {

		protected T infoObject;

		public void initialize(T infoObject) {
			this.infoObject = infoObject;
			setIdFactory(this);
		}

	}
	
	@Override
	public synchronized ObjectId getIdentifierId(ElementInfo object) {
		defaultIdFactory.initialize(null);
		return super.getIdentifierId(object);
		
	}
	
	MorfableIdFactory<?> defaultIdFactory = new MorfableIdFactory<ElementInfo>() {
		@Override
		public ObjectId create(long id, ElementInfo object) {
			return ObjectId.factory(id, object);
		}
	};

	MorfableIdFactory<ClassLoaderInfo> classLoaderIdFactory = new MorfableIdFactory<ClassLoaderInfo>() {
		@Override
		public ObjectId create(long id, ElementInfo object) {
			return new ClassLoaderId(id, infoObject);
		}
	};
	MorfableIdFactory<ClassInfo> classObjectIdFactory = new MorfableIdFactory<ClassInfo>() {
		@Override
		public ObjectId create(long id, ElementInfo object) {
			return new ClassObjectId(id, infoObject);
		}
	};
	MorfableIdFactory<ThreadInfo> threadIdFactory = new MorfableIdFactory<ThreadInfo>() {
		@Override
		public ObjectId create(long id, ElementInfo object) {
			return new ThreadId(id, infoObject);
		}
	};

	public synchronized <O, I extends InfoObjectId<O>> I getIdentifierId(ElementInfo object, O infoObject, MorfableIdFactory<O> morfableIdFactory,
			Class<I> returnClazz) {
		morfableIdFactory.initialize(infoObject);
		
		return fetchIdentifierId(object, returnClazz);
	}
	
	@SuppressWarnings("unchecked")
	private <I extends ObjectId> I checkObjectId(ObjectId objectId, Class<I> returnClazz) {
		if (returnClazz.isInstance(objectId)) {
			return (I) objectId;
		}
		
		// TODO solve this in a standard JDWP way
		throw new RuntimeException("Got an incompatible object identifier. Object ID '" + objectId + "' is not '" + returnClazz + "'");
	}
	
	private <I extends ObjectId> I fetchIdentifierId(ElementInfo object, Class<I> returnClazz) {
		return checkObjectId(super.getIdentifierId(object), returnClazz);
	}
	public <I extends ObjectId> I readIdentifier(ByteBuffer bytes, Class<I> returnClazz) {
		return checkObjectId(readIdentifier(bytes), returnClazz);
	}
	public <I extends ObjectId> I getIdentifierId(ElementInfo object, Class<I> returnClazz) {
		defaultIdFactory.initialize(null);
		return fetchIdentifierId(object, returnClazz);
	}

	public ClassLoaderId getClassLoaderId(ClassLoaderInfo classLoaderInfo) {
		ElementInfo classLoaderObject = VM.getVM().getHeap().get(classLoaderInfo.getClassLoaderObjectRef());
		return getIdentifierId(classLoaderObject, classLoaderInfo, classLoaderIdFactory, ClassLoaderId.class);
	}

	public ClassObjectId getClassObjectId(ClassInfo classInfo) {
		return getIdentifierId(classInfo.getClassObject(), classInfo, classObjectIdFactory, ClassObjectId.class);
	}

	public ThreadId getThreadId(ThreadInfo threadInfo) {
		return getIdentifierId(threadInfo.getThreadObject(), threadInfo, threadIdFactory, ThreadId.class);
	}

}
