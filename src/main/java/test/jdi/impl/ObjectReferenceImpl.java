package test.jdi.impl;

import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.LocalVarInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.InvocationException;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Type;
import com.sun.jdi.Value;

public class ObjectReferenceImpl extends ValueImpl implements ObjectReference {

	private ClassInfo ci;

	public ObjectReferenceImpl(VirtualMachineImpl vm, ClassInfo ci) {
		super(vm);
		this.ci = ci;
	}
	
	private static Map<ClassInfo, ObjectReferenceImpl> allObjectReferences = new ConcurrentHashMap<ClassInfo, ObjectReferenceImpl>();

	public static ObjectReferenceImpl factory(ClassInfo ci, VirtualMachineImpl vm) {
		synchronized (allObjectReferences) {
			if (allObjectReferences.containsKey(ci)) {
				return allObjectReferences.get(ci);
			} else {
				ObjectReferenceImpl objectReferenceImpl = new ObjectReferenceImpl(
						vm, ci);
				allObjectReferences.put(ci, objectReferenceImpl);
				return objectReferenceImpl;
			}
		}
	}

	@Override
	public Type type() {
		// TODO Auto-generated method stub
		return ClassTypeImpl.factory(ci, vm);
	}

	@Override
	public void disableCollection() {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableCollection() {
		// TODO Auto-generated method stub

	}

	@Override
	public int entryCount() throws IncompatibleThreadStateException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Value getValue(Field sig) {
		// TODO [for PJA] this is weird following call always returns zero values ... what is this for? 
		// Can't find any method returning Fields instance I could use for getValueObject()
		Object object = ((FieldImpl)sig).getFieldInfo().getValueObject(ci.createInstanceFields());
		return ValueImpl.factory(object, vm);
	}

	@Override
	public Map<Field, Value> getValues(List<? extends Field> fields) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Value invokeMethod(ThreadReference thread, Method method,
			List<? extends Value> arguments, int options)
			throws InvalidTypeException, ClassNotLoadedException,
			IncompatibleThreadStateException, InvocationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCollected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ThreadReference owningThread()
			throws IncompatibleThreadStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReferenceType referenceType() {
		return ReferenceTypeImpl.factory(ci, vm);
	}

	@Override
	public List<ObjectReference> referringObjects(long maxReferrers) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValue(Field field, Value value) throws InvalidTypeException,
			ClassNotLoadedException {
		// TODO Auto-generated method stub

	}

	@Override
	public long uniqueID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<ThreadReference> waitingThreads()
			throws IncompatibleThreadStateException {
		// TODO Auto-generated method stub
		return null;
	}

}
