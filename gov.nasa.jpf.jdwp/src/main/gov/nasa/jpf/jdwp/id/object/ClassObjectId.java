package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.VM;

/**
 * This class implements the corresponding <code>classObjectID</code> common data type from
 * the JDWP Specification.
 * 
 * Class Object identifier representation.<br/>
 * Since every class object (e.g. SomeClass.class) is represented by a
 * {@link ClassInfo} instance this class was designed to store this relation by
 * extending the {@link InfoObjectId} class.<br/>
 * See {@link ClassObjectId#getClassInfo(ElementInfo)} for further details.
 * <p>
 * <h2>JDWP Specification</h2>
 * Uniquely identifies an object in the target VM that is known to be a class
 * object.
 * </p>
 * 
 * @author stepan
 * 
 */
public class ClassObjectId extends InfoObjectId<ClassInfo> {

  public ClassObjectId(long id, ClassInfo classInfo) {
    this(id, classInfo.getClassObject(), classInfo);
  }

  private ClassObjectId(long id, ElementInfo elementInfo, ClassInfo classInfo) {
    super(Tag.CLASS_OBJECT, id, elementInfo, classInfo);
  }

  public ClassObjectId(long id, ElementInfo elementInfo) {
    this(id, elementInfo, getClassInfo(elementInfo));
  }

  /**
   * This is the way how to get {@link ClassInfo} for the given parameter.<br/>
   * Note that {@link ElementInfo#getClassInfo()} returns the {@link ClassInfo}
   * for the given SUT class object. But here, we want {@link ClassInfo} for the
   * type that the SUT class object represents.
   * 
   * @param elementInfo
   * @return
   */
  private static ClassInfo getClassInfo(ElementInfo elementInfo) {
    int typeNameRef = elementInfo.getReferenceField("name");
    ElementInfo typeName = VM.getVM().getHeap().get(typeNameRef);
    String reflectedTypeString = typeName.asString();
    ClassInfo ci = ClassInfo.getInitializedClassInfo(reflectedTypeString, VM.getVM().getCurrentThread());
    return ci;
  }

  @Override
  protected ClassInfo resolveInfoObject() throws InvalidObject {
    return getClassInfo(get());
  }
}
