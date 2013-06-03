package gov.nasa.jpf.jdwp.util;

import gov.nasa.jpf.jdwp.value.StringRaw;
import gov.nasa.jpf.jvm.ClassFile;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.MethodInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author stepan
 * 
 */
public class VariableTable {

	/**
	 * The number of words in the frame used by arguments. Eight-byte arguments
	 * use two words; all others use one.
	 */
	int argCnt;

	private List<Slot> slots = new ArrayList<VariableTable.Slot>();

	public VariableTable(MethodInfo methodInfo) {
		System.out.println("VARIABLE TABLE CREATION: method: " + methodInfo);

		if (methodInfo.getLocalVars() != null) {
			for (LocalVarInfo localVarInfo : methodInfo.getLocalVars()) {
				new Slot(localVarInfo, methodInfo);
			}
		}
		argCnt = methodInfo.getArgumentsSize(); // TODO this might be wrong ..
												// see a comment bellow
		// according to JDWP argCount is:
		// The number of words in the frame used by arguments. Eight-byte
		// arguments use two words; all others use one.
		// I'm so unsure what exactly this means ... just TODO - has to be
		// tested
	}
	
	public void write (DataOutputStream os) throws IOException {
		os.writeInt(argCnt);
		os.writeInt(slots.size());
		
		for (Slot slot : slots) {
			slot.write(os);
		}
	}

	private class Slot {

		/**
		 * First code index at which the variable is visible, The variable can
		 * be get or set only when the current <code>codeIndex <= </code>
		 * current frame code index <code> < codeIndex + lenth</code>
		 */
		private long codeIndex;

		/**
		 * The variable's name.
		 */
		private String name;

		/**
		 * The variable type's JNI signature.
		 */
		private String signature;

		/**
		 * Unsigned value used in conjunction with <code>codeIndex</code>.
		 */
		private int length;

		/**
		 * The local variable's index in its frame
		 */
		private int slot;

		/**
		 * Creates and registers a slot in a {@link VariableTable} instance.
		 * 
		 * @param localVarInfo
		 *            An instance of {@link LocalVarInfo} from JPF.
		 * @param methodInfo
		 *            An instance of {@link MethodInfo} which this local
		 *            variable belongs to
		 */
		public Slot(LocalVarInfo localVarInfo, MethodInfo methodInfo) {

			// we need to calculate instruction index from accumulated bytecode
			// position
			Instruction startInstruction = methodInstructionDownToPosition(methodInfo, localVarInfo.getStartPC());
			Instruction endInstruction = methodInstructionUpToPosition(methodInfo, localVarInfo.getStartPC() + localVarInfo.getLength() - 1);

			codeIndex = startInstruction.getInstructionIndex();
			name = localVarInfo.getName();
			signature = localVarInfo.getSignature();
			length = (int) (endInstruction.getInstructionIndex() - codeIndex) + 1;
			slot = localVarInfo.getSlotIndex();

			slots.add(this);

			System.out.println("VARIABLE TABLE: index: " + codeIndex + " slot: " + slot + " length: " + length + " name: " + name + " ... localVarInfo: "
					+ localVarInfo);
		}

		/**
		 * Finds an instruction at position which closest from the bottom to the
		 * position given as a parameter.
		 * 
		 * TODO [for PJA] For some reason debug table might return endPC or
		 * startPC that doesn't contain an instruction
		 * 
		 * @see MethodInfo#getInstructionAt(int)
		 * @see ClassFile#parseLocalVarTableAttr(gov.nasa.jpf.classfile.ClassFileReader,
		 *      Object)
		 * 
		 *      TODO handle errors properly
		 * 
		 * @param methodInfo
		 * @param position
		 * @return
		 */
		private Instruction methodInstructionUpToPosition(MethodInfo methodInfo, int position) {
			Instruction[] code = methodInfo.getInstructions();

			Instruction instruction = code[0];

			for (int i = 0; i < code.length; i++) {
				if ((code[i] != null) && (code[i].getPosition() <= position)) {
					instruction = code[i];
				}
			}

			return instruction;
		}

		/**
		 * Finds an instruction at position which closest from the top to the
		 * position given as a parameter.
		 * 
		 * TODO [for PJA] For some reason debug table might return endPC or
		 * startPC that doesn't contain an instruction
		 * 
		 * @see MethodInfo#getInstructionAt(int)
		 * @see ClassFile#parseLocalVarTableAttr(gov.nasa.jpf.classfile.ClassFileReader,
		 *      Object)
		 * 
		 *      TODO handle errors properly
		 * 
		 * @param methodInfo
		 * @param position
		 * @return
		 */
		private Instruction methodInstructionDownToPosition(MethodInfo methodInfo, int position) {
			Instruction[] code = methodInfo.getInstructions();

			Instruction instruction = code[code.length - 1];

			for (int i = code.length - 1; i >= 0; --i) {
				if ((code[i] != null) && (code[i].getPosition() >= position)) {
					instruction = code[i];
				}
			}

			return instruction;
		}

		/**
		 * Outputs the slot into the stream.
		 * 
		 * @param os
		 *            The stream where to output this slot.
		 * @throws IOException
		 *             If an I/O error occurs
		 */
		public void write(DataOutputStream os) throws IOException {
			os.writeLong(codeIndex);
			new StringRaw(name).write(os);
			new StringRaw(signature).write(os);
			os.writeInt(length);
			os.writeInt(slot);
		}
	}

}