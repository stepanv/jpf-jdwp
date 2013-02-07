package test.jdi.impl.request;

import java.util.HashSet;
import java.util.Set;

import test.jdi.impl.VirtualMachineImpl;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.ClassPrepareRequest;

public class ClassPrepareRequestImpl extends EventRequestImpl implements ClassPrepareRequest {

	public ClassPrepareRequestImpl(VirtualMachineImpl vm) {
		super(vm);
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
