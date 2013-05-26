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

	@SuppressWarnings("unchecked")
	public synchronized <O, I extends InfoObjectId<O>> I getIdentifierId(ElementInfo object, O infoObject, MorfableIdFactory<O> morfableIdFactory,
			Class<I> returnClazz) {
		morfableIdFactory.initialize(infoObject);

		ObjectId objectId = super.getIdentifierId(object);

		try {
			// we don't want to slow down in the good case
			return (I) objectId;
		} catch (ClassCastException e) {
			// TODO solve this in a standard JDWP way
			throw new RuntimeException("Got an incompatible object identifier", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <I> I readIdentifier(ByteBuffer bytes, Class<I> returnClazz) {
		ObjectId objectId = readIdentifier(bytes);
		try {
			// we don't want to slow down in the good case
			return (I) objectId;
		} catch (ClassCastException e) {
			// TODO solve this in a standard JDWP way
			throw new RuntimeException("Got an incompatible object identifier", e);
		}
		
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
