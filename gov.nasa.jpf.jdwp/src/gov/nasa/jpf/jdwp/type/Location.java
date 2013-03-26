package gov.nasa.jpf.jdwp.type;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import gov.nasa.jpf.jdwp.command.CommandContextProvider;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId.TypeTag;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;

public class Location {

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
	 
	 public static Location factory(ByteBuffer bytes, CommandContextProvider contextProvider) {
		 
		 contextProvider.getObjectManager().readReferenceTypeId(bytes)
	 }

	public Instruction getInstruction() {
		return instruction;
	}
	
	private TypeTag typeTag;

	public void write(DataOutputStream os) throws IOException {
		if (methodInfo != null) {
			
		}
		
		 = instruction.getMethodInfo();
		 ClassInfo classInfo
		os.write(typeTag.identifier());
		// TODO Auto-generated method stub
		
	}

}
