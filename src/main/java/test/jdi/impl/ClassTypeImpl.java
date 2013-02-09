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
import java.util.Collections;
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

	private static Map<ClassInfo,ClassTypeImpl> allReferenceTypes = new ConcurrentHashMap<ClassInfo,ClassTypeImpl>();

	public static ClassTypeImpl factory(ClassInfo resolvedClassInfo, VirtualMachineImpl vm) {
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
	
	private ClassTypeImpl(ClassInfo resolvedClassInfo, VirtualMachineImpl vm) {
		super(resolvedClassInfo, vm);
		this.vm = vm;
		
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
