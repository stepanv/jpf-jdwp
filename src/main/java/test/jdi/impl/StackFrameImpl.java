package test.jdi.impl;

import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

public class StackFrameImpl implements StackFrame {

	public static final Logger log = org.apache.log4j.Logger.getLogger(StackFrameImpl.class);
	
	private VirtualMachineImpl vm;

	private gov.nasa.jpf.jvm.StackFrame stackFrame;

	private ThreadReferenceImpl threadReference;

	public StackFrameImpl(gov.nasa.jpf.jvm.StackFrame stackFrame,
			ThreadReferenceImpl threadReferenceImpl, VirtualMachineImpl vm) {
		this.vm = vm;
		this.stackFrame = stackFrame;
		this.threadReference = threadReferenceImpl;
	}

	@Override
	public VirtualMachine virtualMachine() {
		return vm;
	}

	@Override
	public List<Value> getArgumentValues() {
		log.debug("method entering");
		return null;
	}

	@Override
	public Value getValue(LocalVariable variable) {
		log.debug("method entering");
		return null;
	}

	@Override
	public Map<LocalVariable, Value> getValues(
			List<? extends LocalVariable> variables) {
		log.debug("method entering");
		return null;
	}

	@Override
	public Location location() {
		log.debug("method entering");
		ClassInfo badCi = threadReference.getThreadInfo().getClassInfo(); // TODO [for PJA] need to find a way how to get correct ClassInfo for this frame
//		stackFrame.getMethodInfo().getClassInfo()
//		stackFrame.getPC().getPrev().getMethodInfo().getClassInfo()
//		stackFrame.getPrevious().getMethodInfo().getClassInfo()
		LocationImpl location = LocationImpl.factory(stackFrame.getPC(), ReferenceTypeImpl.factory(badCi, vm), vm);
		return location;
	}

	@Override
	public void setValue(LocalVariable variable, Value value)
			throws InvalidTypeException, ClassNotLoadedException {
		log.debug("method entering");

	}

	@Override
	public ObjectReference thisObject() {
		log.debug("method entering");
		return null;
	}

	@Override
	public ThreadReference thread() {
		log.debug("method entering");
		return threadReference;
	}

	@Override
	public LocalVariable visibleVariableByName(String name)
			throws AbsentInformationException {
		log.debug("method entering");
		return null;
	}

	@Override
	public List<LocalVariable> visibleVariables()
			throws AbsentInformationException {
		log.debug("method entering");
		return null;
	}

}
