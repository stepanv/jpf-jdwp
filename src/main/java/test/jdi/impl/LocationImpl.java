package test.jdi.impl;

import gov.nasa.jpf.jvm.bytecode.Instruction;

import org.apache.log4j.Logger;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;

public class LocationImpl implements Location {

	public static final Logger log = org.apache.log4j.Logger.getLogger(LocationImpl.class);
	private Instruction instruction;
	private VirtualMachine vm;
	private int lineNumber;
	private ReferenceTypeImpl referenceType;
	
	
	public LocationImpl(Instruction instruction, int lineNumber, ReferenceTypeImpl referenceTypeImpl, VirtualMachine vm) {
		this.setInstruction(instruction);
		this.vm = vm;
		this.lineNumber = lineNumber;
		this.referenceType = referenceTypeImpl;
	}

	@Override
	public VirtualMachine virtualMachine() {
		log.debug("method entering");
		return vm;
	}

	@Override
	public int compareTo(Location o) {
		log.debug("method entering");
		return 0;
	}

	@Override
	public ReferenceType declaringType() {
		log.debug("method entering");
		return null;
	}

	@Override
	public Method method() {
		log.debug("method entering");
		return null;
	}

	@Override
	public long codeIndex() {
		log.debug("method entering");
		return 0;
	}

	@Override
	public String sourceName() throws AbsentInformationException {
		log.debug("method entering");
		return referenceType.name();
	}

	@Override
	public String sourceName(String paramString)
			throws AbsentInformationException {
		log.debug("method entering");
		return null;
	}

	@Override
	public String sourcePath() throws AbsentInformationException {
		log.debug("method entering");
		return null;
	}

	@Override
	public String sourcePath(String paramString)
			throws AbsentInformationException {
		log.debug("method entering");
		return null;
	}

	@Override
	public int lineNumber() {
		log.debug("method entering");
		return lineNumber;
	}

	@Override
	public int lineNumber(String paramString) {
		log.debug("method entering");
		return 0;
	}

	public Instruction getInstruction() {
		return instruction;
	}

	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}
	
}