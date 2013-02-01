package test.jdi.impl;

import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.FieldInfo;
import gov.nasa.jpf.jvm.Heap;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.StaticElementInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.util.LocationSpec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassLoaderReference;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassObjectReference;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.InvocationException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

public class ClassTypeImpl extends ReferenceTypeImpl implements ClassType {

	public static final Logger log = org.apache.log4j.Logger.getLogger(ClassTypeImpl.class);
	
	private StaticElementInfo elInfo;
	private ClassInfo classInfo;
	private VirtualMachine vm;


	private static Map<ClassInfo,ClassTypeImpl> allReferenceTypes = new ConcurrentHashMap<ClassInfo,ClassTypeImpl>();

	public static ClassTypeImpl factory(ClassInfo resolvedClassInfo, VirtualMachine vm) {
		synchronized (allReferenceTypes) {
			if (allReferenceTypes.containsKey(resolvedClassInfo)) {
				return allReferenceTypes.get(resolvedClassInfo);
			} else {
				ClassTypeImpl referenceTypeImpl = new ClassTypeImpl(resolvedClassInfo, vm);
				allReferenceTypes.put(resolvedClassInfo, referenceTypeImpl);
				return referenceTypeImpl;
			}
		}
	}
	
	private ClassTypeImpl(ClassInfo resolvedClassInfo, VirtualMachine vm) {
		super(resolvedClassInfo, vm);
		this.classInfo = resolvedClassInfo;
		this.vm = vm;
	}

	@Override
	public String signature() {
		log.debug("method entering");
		return classInfo.getSignature();
	}

	@Override
	public VirtualMachine virtualMachine() {
		return vm;
	}

	@Override
	public int compareTo(ReferenceType o) {
		log.debug("method entering");
		return 0;
	}

	@Override
	public int modifiers() {
		log.debug("method entering");
		return classInfo.getModifiers();
	}

	@Override
	public boolean isPrivate() {
		log.debug("method entering");
		return false;
	}

	@Override
	public boolean isPackagePrivate() {
		log.debug("method entering");
		return false;
	}

	@Override
	public boolean isProtected() {
		log.debug("method entering");
		return false;
	}

	@Override
	public boolean isPublic() {
		log.debug("method entering");
		return false;
	}

	@Override
	public String name() {
		return classInfo.getName();
	}

	@Override
	public String genericSignature() {
		log.debug("method entering");
		return classInfo.getSignature();
	}

	@Override
	public ClassLoaderReference classLoader() {
		log.debug("method entering");
		return null;
	}

	@Override
	public String sourceName() throws AbsentInformationException {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<String> sourceNames(String paramString)
			throws AbsentInformationException {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<String> sourcePaths(String paramString)
			throws AbsentInformationException {
		log.debug("method entering");
		return null;
	}

	@Override
	public String sourceDebugExtension() throws AbsentInformationException {
		log.debug("method entering");
		return null;
	}

	@Override
	public boolean isStatic() {
		log.debug("method entering");
		return false;
	}

	@Override
	public boolean isAbstract() {
		log.debug("method entering");
		return false;
	}

	@Override
	public boolean isFinal() {
		log.debug("method entering");
		return false;
	}

	@Override
	public boolean isPrepared() {
		log.debug("method entering");
		return false;
	}

	@Override
	public boolean isVerified() {
		log.debug("method entering");
		return false;
	}

	@Override
	public boolean isInitialized() {
		log.debug("method entering");
		return false;
	}

	@Override
	public boolean failedToInitialize() {
		log.debug("method entering");
		return false;
	}

	@Override
	public List<Field> fields() {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<Field> visibleFields() {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<Field> allFields() {
		log.debug("method entering");
		return null;
	}

	private Map<FieldInfo, FieldImpl> fields = new HashMap<FieldInfo, FieldImpl>();
	@Override
	public Field fieldByName(String paramString) {
		FieldInfo fi = classInfo.getDeclaredInstanceField(paramString);
		if (fi == null) {
			fi = classInfo.getInstanceField(paramString);
		}
		if (fi == null) {
			fi = classInfo.getStaticField(paramString);
		}
		if (fi == null) {
			return null;
		}
		
		if (!fields.containsKey(fi)) {
			fields.put(fi, new FieldImpl(fi));
		}
		return fields.get(fi);
	}

	@Override
	public List<Method> methods() {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<Method> visibleMethods() {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<Method> allMethods() {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<Method> methodsByName(String paramString) {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<Method> methodsByName(String paramString1, String paramString2) {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<ReferenceType> nestedTypes() {
		log.debug("method entering");
		return null;
	}

	@Override
	public Value getValue(Field paramField) {
		 FieldImpl fi = (FieldImpl)paramField;
		 FieldInfo ffi = fi.getFieldInfo();
		
		Heap heap = JVM.getVM().getHeap();
	    ElementInfo ei = heap.get(elInfo.getIntField(ffi));
		
	    Value value = new ValueImpl(ei, ffi);
	    //ei.getIntField(paramField.name())
		return value;
	}

	@Override
	public Map<Field, Value> getValues(List<? extends Field> paramList) {
		log.debug("method entering");
		return null;
	}

	@Override
	public ClassObjectReference classObject() {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<Location> allLineLocations() throws AbsentInformationException {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<Location> allLineLocations(String paramString1,
			String paramString2) throws AbsentInformationException {
		log.debug("method entering");
		return null;
	}

	
	@Override
	public List<Location> locationsOfLine(int paramInt)
			throws AbsentInformationException {
		List<Location> locations = new ArrayList<Location>();
		// TODO [for PJA] what to do, if location contain more than 1 instruction?
		// in such case, we would hit the breakpoint more times ... how to handle this?
		for (Instruction instruction : classInfo.getMatchingInstructions(LocationSpec.createLocationSpec(classInfo.getSourceFileName() + ":" + paramInt))) {
			log.debug("Requesting location at reference type: " + name() +" line: " + paramInt);
			locations.add(LocationImpl.factory(instruction, this, vm));
			log.debug("Returning just one location in the list .. TODO");
			return locations; // TODO we have to return all instructions and do smarter breakpoints in BreakpointManager
		}
		return locations;
	}

	@Override
	public List<Location> locationsOfLine(String paramString1,
			String paramString2, int paramInt)
			throws AbsentInformationException {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<String> availableStrata() {
		log.debug("method entering");
		return null;
	}

	@Override
	public String defaultStratum() {
		return "Java"; //TODO we should get this info dynamically (see 'strata' section for Location JavaDoc) ... JPF doesn't work with strata at all though.
	}

	@Override
	public List<ObjectReference> instances(long paramLong) {
		log.debug("method entering");
		return null;
	}

	@Override
	public int majorVersion() {
		log.debug("method entering");
		return 0;
	}

	@Override
	public int minorVersion() {
		log.debug("method entering");
		return 0;
	}

	@Override
	public int constantPoolCount() {
		log.debug("method entering");
		return 0;
	}

	@Override
	public byte[] constantPool() {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<InterfaceType> allInterfaces() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method concreteMethodByName(String name, String signature) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<InterfaceType> interfaces() {
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
	public boolean isEnum() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ObjectReference newInstance(ThreadReference thread, Method method,
			List<? extends Value> arguments, int options)
			throws InvalidTypeException, ClassNotLoadedException,
			IncompatibleThreadStateException, InvocationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValue(Field field, Value value) throws InvalidTypeException,
			ClassNotLoadedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ClassType> subclasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClassType superclass() {
		// TODO Auto-generated method stub
		return null;
	}

}
