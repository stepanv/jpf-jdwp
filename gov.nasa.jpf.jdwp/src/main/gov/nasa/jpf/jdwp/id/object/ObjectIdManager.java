package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.id.IdManager.IdFactory;
import gov.nasa.jpf.jdwp.id.object.special.NullObjectId;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.DynamicElementInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectIdManager {

  final static Logger logger = LoggerFactory.getLogger(ObjectIdManager.class);

  private IdFactory<ObjectId, ElementInfo> dynamicIdFactory;

  public void setDynamicIdFactory(IdFactory<ObjectId, ElementInfo> dynamicIdFactory) {
    this.dynamicIdFactory = dynamicIdFactory;
  }

  public ObjectIdManager() {
  }

  public abstract class MorfableIdFactory<T> implements IdFactory<ObjectId, ElementInfo> {

    protected T infoObject;

    public void initialize(T infoObject) {
      this.infoObject = infoObject;
      setDynamicIdFactory(this);
    }

  }

  private Map<Long, ObjectId> idMap = new HashMap<Long, ObjectId>();

  @SuppressWarnings("unchecked")
  private <I extends ObjectId> I checkAndCastObjectId(ObjectId objectId, Class<I> returnClazz) {
    if (returnClazz.isInstance(objectId)) {
      return (I) objectId;
    }

    // TODO solve this in a standard JDWP way
    throw new RuntimeException("Got an incompatible object identifier. Object ID '" + objectId + "' is not '" + returnClazz + "'");
  }

  public synchronized ObjectId getIdentifierId(ElementInfo object) {
    defaultIdFactory.initialize(null);
    return getIdentifierIdInitializedFactory(object);
  }

  public synchronized <O, I extends InfoObjectId<O>> I getIdentifierId(ElementInfo object, O infoObject,
                                                                       MorfableIdFactory<O> morfableIdFactory, Class<I> returnClazz) {
    morfableIdFactory.initialize(infoObject);

    return checkAndCastObjectId(this.getIdentifierIdInitializedFactory(object), returnClazz);
  }

  public synchronized <I extends ObjectId> I getIdentifierId(ElementInfo object, Class<I> returnClazz) {
    return checkAndCastObjectId(this.getIdentifierId(object), returnClazz);
  }

  private synchronized ObjectId getIdentifierIdInitializedFactory(ElementInfo object) {
    Long id;
    if (object instanceof DynamicElementInfo) {
      id = (long) object.getObjectRef();
    } else {
      // TODO (see ObjectId#get()) - we probably want to use DynamicElementInfo
      // everywhere
      // We don't want StaticElementInfo here!!!
      throw new RuntimeException("We have StaticElementInfo instead of DynamicElementInfo! Object: " + object);
    }
    if (idMap.containsKey(id)) {
      ObjectId objectId = idMap.get(id);
      if (!object.getClassInfo().equals(objectId.get().getClassInfo())) {
        logger.error("Object {} is not object {} for objectId {}", object, objectId.get(), objectId);
        throw new RuntimeException(String.format("Object %s is not object %s for objectId %s", object, objectId.get(), objectId));
      }
      return objectId;
    } else {
      ObjectId objectId = dynamicIdFactory.create(id, object);
      idMap.put(id, objectId);
      logger.debug("Created object ID: {}, (identifier: {}) object: {}, class: {}, classInfo: {}", id, objectId, object, object.getClass(),
                   ((ElementInfo) object).getClassInfo());
      return objectId;
    }
  }

  MorfableIdFactory<?> defaultIdFactory = new MorfableIdFactory<ElementInfo>() {
    @Override
    public ObjectId create(long id, ElementInfo object) {
      return ObjectId.objectIdFactory(id, object);
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

  public synchronized ObjectId readIdentifier(ByteBuffer bytes) {
    // TODO throw ErrorType.INVALID_OBJECT
    Long id = bytes.getLong();
    if (id == 0) {
      return NullObjectId.getInstance();
    }
    return idMap.get(id);
  }

  public synchronized <I extends ObjectId> I readIdentifier(ByteBuffer bytes, Class<I> returnClazz) {
    return checkAndCastObjectId(readIdentifier(bytes), returnClazz);
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
