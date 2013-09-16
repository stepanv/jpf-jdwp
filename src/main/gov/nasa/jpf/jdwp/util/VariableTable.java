/* 
   Copyright (C) 2013 Stepan Vavra

This file is part of (Java Debug Wire Protocol) JDWP for 
Java PathFinder (JPF) project.

JDWP for JPF is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

JDWP for JPF is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 
 */

package gov.nasa.jpf.jdwp.util;

import gov.nasa.jpf.jdwp.exception.AbsentInformationException;
import gov.nasa.jpf.jdwp.value.JdwpString;
import gov.nasa.jpf.jvm.ClassFile;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.MethodInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author stepan
 * 
 */
public class VariableTable {

  final static Logger logger = LoggerFactory.getLogger(VariableTable.class);

  /**
   * The number of words in the frame used by arguments. Eight-byte arguments
   * use two words; all others use one.
   */
  int argCnt;

  private List<Slot> slots = new ArrayList<VariableTable.Slot>();

  public VariableTable(MethodInfo methodInfo) throws AbsentInformationException {
    logger.debug("Variable Table creation for method: {}", methodInfo);

    if (methodInfo.getLocalVars() != null) {
      for (LocalVarInfo localVarInfo : methodInfo.getLocalVars()) {
        slots.add(new Slot(localVarInfo, methodInfo));
      }
    } else {
      throw new AbsentInformationException("Method doesn't have variable information present: " + methodInfo
          + " (Try to add debug info for the compilation.)");
    }
    argCnt = methodInfo.getArgumentsSize();
  }

  /**
   * Writes the variable table to the given output stream
   * 
   * @param os
   *          The output stream
   * @throws IOException
   *           If I/O error occurs
   */
  public void write(DataOutputStream os) throws IOException {
    write(os, false);
  }

  /**
   * Writes the variable table including generic information to the given output
   * stream
   * 
   * @param os
   *          The output stream
   * @throws IOException
   *           If I/O error occurs
   */
  public void writeGeneric(DataOutputStream os) throws IOException {
    write(os, true);
  }

  private void write(DataOutputStream os, boolean withGeneric) throws IOException {
    os.writeInt(argCnt);
    os.writeInt(slots.size());

    for (Slot slot : slots) {
      if (withGeneric) {
        slot.writeGeneric(os);
      } else {
        slot.write(os);
      }
    }
  }

  private class Slot {

    /**
     * First code index at which the variable is visible, The variable can be
     * get or set only when the current <code>codeIndex <= </code> current frame
     * code index <code> < codeIndex + lenth</code>
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
     * The variable type's generic signature or an empty string if there is
     * none.
     */
    public String genericSignature;

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
     *          An instance of {@link LocalVarInfo} from JPF.
     * @param methodInfo
     *          An instance of {@link MethodInfo} which this local variable
     *          belongs to
     */
    public Slot(LocalVarInfo localVarInfo, MethodInfo methodInfo) {

      // we need to calculate instruction index from accumulated bytecode
      // position
      Instruction startInstruction = methodInstructionDownToPosition(methodInfo, localVarInfo.getStartPC());
      Instruction endInstruction = methodInstructionUpToPosition(methodInfo, localVarInfo.getStartPC() + localVarInfo.getLength() - 1);

      codeIndex = startInstruction.getInstructionIndex();
      name = localVarInfo.getName();
      signature = localVarInfo.getSignature();
      genericSignature = localVarInfo.getGenericSignature();
      length = (int) (endInstruction.getInstructionIndex() - codeIndex) + 1;
      slot = localVarInfo.getSlotIndex();

      logger.debug("VARIABLE TABLE: index: {} slot: {} length: {} name: {} ... localVarInfo: {}, generic signature: {}", codeIndex, slot,
                   length, name, localVarInfo, genericSignature);
    }

    /**
     * Finds an instruction at position which closest from the bottom to the
     * position given as a parameter.
     * 
     * @see MethodInfo#getInstructionAt(int)
     * @see ClassFile#parseLocalVarTableAttr(gov.nasa.jpf.classfile.ClassFileReader,
     *      Object)
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
     * @see MethodInfo#getInstructionAt(int)
     * @see ClassFile#parseLocalVarTableAttr(gov.nasa.jpf.classfile.ClassFileReader,
     *      Object)
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
     *          The stream where to output this slot.
     * @throws IOException
     *           If an I/O error occurs
     */
    public void write(DataOutputStream os) throws IOException {
      write(os, false);
    }

    /**
     * Outputs the slot including generic information into the stream.
     * 
     * @param os
     *          The stream where to output this slot.
     * @throws IOException
     *           If an I/O error occurs
     */
    public void writeGeneric(DataOutputStream os) throws IOException {
      write(os, true);
    }

    private void write(DataOutputStream os, boolean withGeneric) throws IOException {
      os.writeLong(codeIndex);
      JdwpString.write(name, os);
      JdwpString.write(signature, os);
      if (withGeneric) {
        JdwpString.writeNullAsEmpty(genericSignature, os);
      }
      os.writeInt(length);
      os.writeInt(slot);
    }

  }

}
