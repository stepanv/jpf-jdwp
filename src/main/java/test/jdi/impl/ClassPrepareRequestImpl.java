package test.jdi.impl;

import java.util.HashSet;
import java.util.Set;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.InvalidRequestStateException;

public class ClassPrepareRequestImpl implements ClassPrepareRequest {

	private VirtualMachineImpl vm;

	public ClassPrepareRequestImpl(VirtualMachineImpl vm) {
		this.vm = vm;
	}

	@Override
	public void addCountFilter(int arg1) throws InvalidRequestStateException {
		// TODO Auto-generated method stub

	}

	boolean enabled = false;
	private int suspendPolicy;

	@Override
	public void disable() {
		enabled = false;
	}

	@Override
	public void enable() {
		enabled = true;

	}

	@Override
	public Object getProperty(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void putProperty(Object key, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEnabled(boolean arg1) {
		enabled = arg1;
	}

	@Override
	public void setSuspendPolicy(int arg1) {
		suspendPolicy = arg1;
	}

	@Override
	public int suspendPolicy() {
		return suspendPolicy;
	}

	@Override
	public VirtualMachine virtualMachine() {
		return vm;
	}

	@Override
	public void addClassExclusionFilter(String arg1) {
		// TODO Auto-generated method stub

	}

	Set<ReferenceType> classFilterReference = new HashSet<ReferenceType>();

	@Override
	public void addClassFilter(ReferenceType arg1) {
		classFilterReference.add(arg1);
	}

	Set<String> classFilterString = new HashSet<String>();

	@Override
	public void addClassFilter(String arg1) {
		classFilterString.add(arg1);
	}

	Set<String> sourceNameFilter = new HashSet<String>();

	@Override
	public void addSourceNameFilter(String arg1) {
		sourceNameFilter.add(arg1);
	}

	public Set<ReferenceType> getClassFilterReference() {
		return classFilterReference;
	}

	public Set<String> getClassFilterString() {
		return classFilterString;
	}

	public Set<String> getSourceNameFilter() {
		return sourceNameFilter;
	}
	
	

}
