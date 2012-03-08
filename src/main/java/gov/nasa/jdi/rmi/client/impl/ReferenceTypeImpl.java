package gov.nasa.jdi.rmi.client.impl;

import gov.nasa.jdi.rmi.common.ReferenceTypeRemote;

import java.util.List;
import java.util.Map;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassLoaderReference;
import com.sun.jdi.ClassObjectReference;
import com.sun.jdi.Field;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

public class ReferenceTypeImpl implements ReferenceType {

	private ReferenceTypeRemote rtr;

	public ReferenceTypeImpl(ReferenceTypeRemote rtr) {
		this.rtr = rtr;
	}

	@Override
	public String signature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VirtualMachine virtualMachine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareTo(ReferenceType o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int modifiers() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isPrivate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPackagePrivate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPublic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String genericSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClassLoaderReference classLoader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sourceName() throws AbsentInformationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sourceNames(String paramString)
			throws AbsentInformationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sourcePaths(String paramString)
			throws AbsentInformationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sourceDebugExtension() throws AbsentInformationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isStatic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAbstract() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFinal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPrepared() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isVerified() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInitialized() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean failedToInitialize() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Field> fields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Field> visibleFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Field> allFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Field fieldByName(String paramString) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Method> methods() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Method> visibleMethods() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Method> allMethods() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Method> methodsByName(String paramString) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Method> methodsByName(String paramString1, String paramString2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ReferenceType> nestedTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Value getValue(Field paramField) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Field, Value> getValues(List<? extends Field> paramList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClassObjectReference classObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Location> allLineLocations() throws AbsentInformationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Location> allLineLocations(String paramString1,
			String paramString2) throws AbsentInformationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Location> locationsOfLine(int paramInt)
			throws AbsentInformationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Location> locationsOfLine(String paramString1,
			String paramString2, int paramInt)
			throws AbsentInformationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> availableStrata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String defaultStratum() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ObjectReference> instances(long paramLong) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int majorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int minorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int constantPoolCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] constantPool() {
		// TODO Auto-generated method stub
		return null;
	}

}