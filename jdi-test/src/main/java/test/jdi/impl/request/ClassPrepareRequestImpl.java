package test.jdi.impl.request;

import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.JVM;

import java.util.HashSet;
import java.util.Set;

import test.jdi.impl.EventRequestManagerImpl.EventRequestContainer;
import test.jdi.impl.ClassTypeImpl;
import test.jdi.impl.VirtualMachineImpl;
import test.jdi.impl.event.ClassPrepareEventImpl;
import test.jdi.impl.event.EventImpl;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;

public class ClassPrepareRequestImpl extends EventRequestImpl implements ClassPrepareRequest {

	public ClassPrepareRequestImpl(VirtualMachineImpl vm, EventRequestContainer<? extends EventRequest> classPrepareRequestContainer) {
		super(vm, classPrepareRequestContainer);
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

	@Override
	protected EventImpl conditionallyGenerateEvent(VirtualMachineImpl vm, JVM jvm) {
		ClassInfo lastClassInfo = jvm.getLastClassInfo();
		
		for (String classFilter : classFilterString) {
			if (simpleMatch(lastClassInfo.getName(), classFilter)) {
				return new ClassPrepareEventImpl(vm,
						jvm.getLastThreadInfo(), this,
						ClassTypeImpl.factory(lastClassInfo, vm));
			}
		}
		return null;
	}
	
	

}
