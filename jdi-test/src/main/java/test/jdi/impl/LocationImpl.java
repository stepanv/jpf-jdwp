package test.jdi.impl;

import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;

public class LocationImpl extends MirrorImpl implements Location {

	public static final Logger log = org.apache.log4j.Logger.getLogger(LocationImpl.class);
	private Instruction instruction;
	private ReferenceTypeImpl referenceType;
	
	
	private LocationImpl(Instruction instruction, ReferenceTypeImpl referenceTypeImpl, VirtualMachineImpl vm) {
		super(vm);
		this.instruction = instruction;
		this.referenceType = referenceTypeImpl;
	}
	
	private static Map<Instruction,LocationImpl> allLocations = new ConcurrentHashMap<Instruction,LocationImpl>();

	public static LocationImpl factory(Instruction instruction, ReferenceTypeImpl referenceTypeImpl, VirtualMachineImpl vm) {
		synchronized (allLocations) {
			if (allLocations.containsKey(instruction)) {
				return allLocations.get(instruction);
			} else {
				LocationImpl locationImpl = new LocationImpl(instruction, referenceTypeImpl, vm);
				allLocations.put(instruction, locationImpl);
				return locationImpl;
			}
		}
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
		return MethodImpl.factory(instruction.getMethodInfo(), vm);
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
		return instruction.getSourceLocation(); // TODO is not possibly correct
	}

	@Override
	public String sourcePath() throws AbsentInformationException {
		log.debug("method entering");
		
		ClassInfo ci = instruction.getMethodInfo().getClassInfo();
	    if (ci != null) {
	      return ci.getSourceFileName();
	    } else {
	    	throw new AbsentInformationException("Cannot fetch source path for synthetic method!");
	    }
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
		return instruction.getLineNumber();
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