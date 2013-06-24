package gov.nasa.jpf.jdwp.type;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import gov.nasa.jpf.jdwp.command.CommandContextProvider;
import gov.nasa.jpf.jdwp.exception.InvalidMethodId;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId.TypeTag;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

public class Location {

	public boolean equals(Location location) {
		if (location == null) {
			return false;
		}
		return instruction == location.instruction;
	}

	private Instruction instruction;
	private MethodInfo methodInfo;
	private int index;

	public Location(MethodInfo methodInfo, int index, Instruction instruction) {
		this.methodInfo = methodInfo;
		this.index = index;
		this.instruction = instruction;
	}

	public static Location factory(Instruction instruction) {
		return new Location(instruction.getMethodInfo(), instruction.getInstructionIndex(), instruction);
	}
	
	public static Location factorySafe(Instruction instruction, ThreadInfo threadInfo) {
		while (instruction.getMethodInfo() == null || instruction.getMethodInfo().getClassInfo() == null) {
			instruction = instruction.getNext(threadInfo);
		}
		return factory(instruction);
	}
	
	private static MethodInfo methodInfoLookup(ClassInfo classInfo, long id) throws JdwpError {
		System.out.println("looking for METHOD global id: " + id + " of CLASS: " + classInfo);
		  for (MethodInfo methodInfo : classInfo.getDeclaredMethodInfos()) {
			  if (id == methodInfo.getGlobalId()) {
				  System.out.println("METHOD found: " + methodInfo);
				  return methodInfo;
			  }
		  }
		  // also try super types
		  if (classInfo.getSuperClass() != null) {
			  return methodInfoLookup(classInfo.getSuperClass(), id);
		  }
		  throw new InvalidMethodId(id);
	}

	public static Location factory(ByteBuffer bytes, CommandContextProvider contextProvider) throws JdwpError {
		byte typeTag = bytes.get(); // TODO we have unique id for all referenceTypeIds regardless of its type tag
		ReferenceTypeId referenceTypeId = contextProvider.getObjectManager().readReferenceTypeId(bytes);
		
		ClassInfo classInfo = referenceTypeId.get();
		
		long id = bytes.getLong();
		
		long index = bytes.getLong();
		
		MethodInfo methodInfo = methodInfoLookup(classInfo, id);
		
		return new Location(methodInfo, (int) index, methodInfo.getInstruction((int) index));
	}

	public Instruction getInstruction() {
		return instruction;
	}

	private TypeTag typeTag;

	public void write(DataOutputStream os) throws IOException {
		if (methodInfo != null) {

		}
		JdwpObjectManager objectManager = JdwpObjectManager.getInstance();

		ClassInfo classInfo = methodInfo.getClassInfo();
		objectManager.getReferenceTypeId(classInfo).writeTagged(os);
		os.writeLong(methodInfo.getGlobalId());
		//objectManager.getObjectId(methodInfo).write(os);
		os.writeLong(index);

	}

	@Override
	public String toString() {
		return super.toString() + ", instruction: " + instruction + ", file: " + instruction.getFileLocation() + ", methodId: " + methodInfo.getGlobalId() + ", index: " + index;
	}

}
