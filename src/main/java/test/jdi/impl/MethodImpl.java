package test.jdi.impl;

import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;

public class MethodImpl implements Method {

	private MethodInfo methodInfo;
	private VirtualMachine vm;

	private MethodImpl(MethodInfo methodInfo, VirtualMachine vm) {
		this.methodInfo = methodInfo;
		this.vm = vm;
	}

	private static Map<MethodInfo,MethodImpl> allMethods = new ConcurrentHashMap<MethodInfo,MethodImpl>();

	public static MethodImpl factory(MethodInfo methodInfo, VirtualMachine vm) {
		synchronized (allMethods) {
			if (allMethods.containsKey(methodInfo)) {
				return allMethods.get(methodInfo);
			} else {
				MethodImpl methodImpl = new MethodImpl(methodInfo, vm);
				allMethods.put(methodInfo, methodImpl);
				return methodImpl;
			}
		}
	}
	
	@Override
	public ReferenceType declaringType() {
		// TODO [for PJA] getClassInfo() sometimes returns null .. how is that possible? bug?
		return ReferenceTypeImpl.factory(methodInfo.getClassInfo(), vm);
	}

	@Override
	public String genericSignature() {
		return methodInfo.getGenericSignature();
	}

	@Override
	public boolean isFinal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStatic() {
		return methodInfo.isStatic();
	}

	@Override
	public boolean isSynthetic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String name() {
		return methodInfo.getName();
	}

	@Override
	public String signature() {
		return methodInfo.getSignature();
	}

	@Override
	public VirtualMachine virtualMachine() {
		return vm;
	}

	@Override
	public boolean isPackagePrivate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPrivate() {
		return methodInfo.isPrivate();
	}

	@Override
	public boolean isProtected() {
		return methodInfo.isProtected();
	}

	@Override
	public boolean isPublic() {
		return methodInfo.isPublic();
	}

	@Override
	public int modifiers() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int compareTo(Method o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Location> allLineLocations() throws AbsentInformationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Location> allLineLocations(String stratum, String sourceName)
			throws AbsentInformationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> argumentTypeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Type> argumentTypes() throws ClassNotLoadedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LocalVariable> arguments() throws AbsentInformationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] bytecodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAbstract() {
		return methodInfo.isAbstract();
	}

	@Override
	public boolean isBridge() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConstructor() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNative() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isObsolete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStaticInitializer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSynchronized() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isVarArgs() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Location location() {
		return LocationImpl.factory(methodInfo.getLastInsn(), ReferenceTypeImpl.factory(methodInfo.getClassInfo(), vm), vm);
	}

	@Override
	public Location locationOfCodeIndex(long codeIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Location> locationsOfLine(int lineNumber)
			throws AbsentInformationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Location> locationsOfLine(String stratum, String sourceName,
			int lineNumber) throws AbsentInformationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type returnType() throws ClassNotLoadedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String returnTypeName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LocalVariable> variables() throws AbsentInformationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LocalVariable> variablesByName(String name)
			throws AbsentInformationException {
		// TODO Auto-generated method stub
		return null;
	}

}
