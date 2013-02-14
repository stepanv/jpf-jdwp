package test.jdi.impl;

import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.FieldInfo;
import gov.nasa.jpf.jvm.LocalVarInfo;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	private Map<LocalVarInfo, LocalVariableImpl> localVariables = new HashMap<LocalVarInfo, LocalVariableImpl>();

	public StackFrameImpl(gov.nasa.jpf.jvm.StackFrame stackFrame,
			ThreadReferenceImpl threadReferenceImpl, VirtualMachineImpl vm) {
		this.vm = vm;
		this.stackFrame = stackFrame;
		this.threadReference = threadReferenceImpl;
		
		for (LocalVarInfo var : this.stackFrame.getLocalVars()) {
			localVariables.put(var, new LocalVariableImpl(var, this, vm));
		}
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
		LocalVarInfo var = ((LocalVariableImpl)variable).localVarInfo;
		Object object = stackFrame.getLocalValueObject(var);
		return ValueImpl.factory(object, vm);
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
//		stackFrame.getMethodInfo().getClassInfo()
//		stackFrame.getPC().getPrev().getMethodInfo().getClassInfo()
//		stackFrame.getPrevious().getMethodInfo().getClassInfo()
		Instruction instruction = stackFrame.getPC();
		
		while (instruction.getMethodInfo() == null || instruction.getMethodInfo().getClassInfo() == null) {
			instruction = instruction.getNext(threadReference.getThreadInfo());
		}
		log.debug("Using instruction: " + instruction + " at line: " + instruction.getLineNumber());
		LocationImpl location = LocationImpl.factory(instruction, ClassTypeImpl.factory(instruction.getMethodInfo().getClassInfo(), vm), vm);
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
		return ObjectReferenceImpl.factory(stackFrame.getClassInfo(), vm);
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
		return new ArrayList<LocalVariable>(localVariables.values());
	}

}
