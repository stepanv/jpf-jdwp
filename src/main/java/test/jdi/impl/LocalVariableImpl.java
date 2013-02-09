package test.jdi.impl;

import gov.nasa.jpf.jvm.LocalVarInfo;

import org.apache.log4j.Logger;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Type;

public class LocalVariableImpl extends MirrorImpl implements LocalVariable {

	protected LocalVarInfo localVarInfo;

	private StackFrameImpl stackFrame;

	private Type type;
	
	public static final Logger log = org.apache.log4j.Logger.getLogger(LocalVariableImpl.class);

	public LocalVariableImpl(LocalVarInfo localVarInfo, StackFrameImpl stackFrame, VirtualMachineImpl vm) {
		super(vm);
		this.localVarInfo = localVarInfo;
		this.stackFrame = stackFrame;
		
		switch (localVarInfo.getSignature().charAt(0)) {
        case 'Z':
        case 'B':
        case 'C':
        case 'S':
          type = null;
        case 'I':
          type = new IntegerTypeImpl(vm); break;
        case 'J':
        case 'F':
        case 'D':
        default:  // reference
        	 type = null;
      }
	}

	@Override
	public int compareTo(LocalVariable o) {
		log.debug("method entering");
		return 0;
	}

	@Override
	public String genericSignature() {
		// TODO [for JPA] again generic signature returns an empty string ... is this really ok?
		if ("".equals(localVarInfo.getGenericSignature())) {
			return null;
		}
		return localVarInfo.getGenericSignature();
	}

	@Override
	public boolean isArgument() {
		log.debug("method entering");
		return false;
	}

	@Override
	public boolean isVisible(StackFrame frame) {
		log.debug("method entering");
		return false;
	}

	@Override
	public String name() {
		return localVarInfo.getName();
	}

	@Override
	public String signature() {
		return localVarInfo.getSignature();
	}

	@Override
	public Type type() throws ClassNotLoadedException {
		log.debug("method entering");
		return type;
	}

	@Override
	public String typeName() {
		return localVarInfo.getType();
	}

}
