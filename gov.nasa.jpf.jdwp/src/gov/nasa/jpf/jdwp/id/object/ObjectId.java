package gov.nasa.jpf.jdwp.id.object;

import gov.nasa.jpf.jdwp.command.ObjectReferenceCommand;
import gov.nasa.jpf.jdwp.exception.InvalidObject;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;
import gov.nasa.jpf.jdwp.id.Identifier;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.TaggableIdentifier;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Fields;
import gov.nasa.jpf.vm.Heap;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

/**
 * <p>
 * {@link ObjectId} class stands for all the elements in the JPF that are
 * accessible from the SUT.<br/>
 * The {@link ElementInfo} generic type of {@link TaggableIdentifier} forces all
 * instances of classes and subclasses of {@link ObjectId} to represent an
 * {@link ElementInfo}.<br/>
 * There are several subclasses of this class that represent only specific
 * objects in SUT (like {@link ThreadId} represents {@link Thread} which is
 * represented by {@link ThreadInfo}) which are required by the JDWP
 * Specification. Nevertheless, those subclasses are sometimes treated by JPDA
 * as {@link ObjectId} instances as well.
 * 
 * <h2>JDWP Specification</h2>
 * Uniquely identifies an object in the target VM. A particular object will be
 * identified by exactly one objectID in JDWP commands and replies throughout
 * its lifetime (or until the objectID is explicitly disposed). An ObjectID is
 * not reused to identify a different object unless it has been explicitly
 * disposed, regardless of whether the referenced object has been garbage
 * collected. An objectID of 0 represents a null object.<br/>
 * 
 * Note that the existence of an object ID does not prevent the garbage
 * collection of the object. Any attempt to access a a garbage collected object
 * with its object ID will result in the {@link ErrorType#INVALID_OBJECT} error
 * code. Garbage collection can be disabled with the
 * {@link ObjectReferenceCommand#DISABLECOLLECTION} command, but it is not
 * usually necessary to do so.
 * </p>
 * 
 * @author stepan
 * 
 */
public class ObjectId extends TaggableIdentifier<ElementInfo> implements Value {

	private Tag tag;

	public ObjectId(Tag tag, long id, ElementInfo object) {
		super(id, object);
		this.tag = tag;
	}

	@Override
	public Tag getIdentifier() {
		return tag;
	}

	@Override
	public void push(StackFrame frame) throws InvalidObject {
		int ref = ((ElementInfo) this.get()).getObjectRef();
		frame.pushRef(ref);
	}

	@Override
	public void modify(StackFrame stackFrame, int slotIndex) throws InvalidObject {
		int ref = ((ElementInfo) this.get()).getObjectRef();
		stackFrame.setLocalVariable(slotIndex, ref, true);

	}

	@Override
	public void modify(Fields fields, int index) throws InvalidObject {
		int ref = get().getObjectRef();
		fields.setReferenceValue(index, ref);
	}

	@Override
	public void disableCollection() throws InvalidObject {
		ElementInfo elementInfo = get();
		Heap heap = VM.getVM().getHeap();
		heap.registerPinDown(elementInfo.getObjectRef());

		super.disableCollection();
	}

	@Override
	public void enableCollection() throws InvalidObject {
		ElementInfo elementInfo = get();
		Heap heap = VM.getVM().getHeap();
		heap.releasePinDown(elementInfo.getObjectRef());

		super.enableCollection();
	}

	@Override
	public boolean isNull() {
		if (super.isNull()) {
			return true;
		}
		try {
			ElementInfo elementInfo = get();
			boolean isLiving = elementInfo != null && elementInfo.getObjectRef() >= 0;
			return !isLiving;
		} catch (InvalidObject e) {
			// this won't happen unless JPF is running and the object was
			// collected between the isNull() call and the get() right after it.
			return true;
		}
	}

	/**
	 * Factory that creates JDWP object identifier for the given parameter.<br/>
	 * Note that this factory should be used only and only if the given object
	 * doesn't have a JDWP identifier yet (an instance of {@link ObjectId} or
	 * it's subclasses).
	 * 
	 * @see JdwpObjectManager#getObjectId(ElementInfo)
	 * 
	 * @param id
	 *            Unique id that is used in the JDWP protocol to represent the
	 *            given object
	 * @param object
	 *            The object to be represented by the result of this factory
	 * @return The {@link Identifier} instance of the given object
	 */
	static ObjectId objectIdFactory(long id, ElementInfo object) {
		ClassInfo classInfo = object.getClassInfo();

		/*
		 * Here, we need to dynamically find whether the object is more than
		 * just a normal object. It is important to understand, that methods
		 * like classInfo.isThreadClassInfo() are misleading since we can have
		 * also subclasses of standard java.lang classes.
		 */

		if (classInfo.isArray()) {
			return new ArrayId(id, object);
		} else if (classInfo.isInstanceOf("java.lang.Thread")) {
			return new ThreadId(id, object);
		} else if (classInfo.isInstanceOf("java.lang.String")) {
			return new StringId(id, object);
		} else if (classInfo.isInstanceOf("java.lang.Class")) {
			return new ClassObjectId(id, object);
		} else if (classInfo.isInstanceOf("java.lang.ThreadGroup")) {
			return new ThreadGroupId(id, object);
		} else if (classInfo.isInstanceOf("java.lang.ClassLoader")) {
			return new ClassLoaderId(id, object);
		} else {
			// any other ElementInfos don't have a specific representation in
			// the JDWP Specification
			return new ObjectId(Tag.OBJECT, id, object);
		}
	}
}
