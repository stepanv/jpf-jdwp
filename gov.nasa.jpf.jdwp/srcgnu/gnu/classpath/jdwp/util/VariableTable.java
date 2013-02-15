/* VariableTable.java -- A class representing a Variable Table for a method
   Copyright (C) 2005, 2007 Free Software Foundation

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */

package gnu.classpath.jdwp.util;

import gov.nasa.jpf.jvm.LocalVarInfo;
import gov.nasa.jpf.jvm.MethodInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.SliderUI;

/**
 * A class representing a Variable Table for a method.
 * 
 * @author Aaron Luchko <aluchko@redhat.com>
 */
public class VariableTable {

	public VariableTable(MethodInfo methodInfo) {
		for (LocalVarInfo localVarInfo : methodInfo.getLocalVars()) {
			  this.new Slot(localVarInfo);
		  }
		argCount = methodInfo.getArgumentsSize(); //TODO this might be wrong .. see a comment bellow
		// according to JDWP argCount is:
		// The number of words in the frame used by arguments. Eight-byte arguments use two words; all others use one.   
		// I'm so unsure what exactly this means ... just TODO - has to be tested
	}

	private List<Slot> slots = new ArrayList<VariableTable.Slot>();
	private int argCount;
	
	public class Slot {
		
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
		 */
		public Slot(LocalVarInfo localVarInfo) {
			codeIndex = localVarInfo.getStartPC();
			name = localVarInfo.getName();
			signature = localVarInfo.getSignature();
			length = localVarInfo.getLength();
			slot = localVarInfo.getSlotIndex();

			VariableTable.this.slots.add(this);
		}
	}

	/**
	 * Writes this line table to the given DataOutputStream.
	 * 
	 * @param os
	 *            the stream to write it to
	 * @throws IOException
	 */
	public void write(DataOutputStream os) throws IOException {
		os.writeInt(argCount);
		os.writeInt(slots.size());
		for (Slot slot : slots) {
			os.writeLong(slot.codeIndex); // 2 words
			JdwpString.writeString(os, slot.name);
			JdwpString.writeString(os, slot.signature);
			os.writeInt(slot.length); // 1 word
			os.writeInt(slot.slot); // 1 word
		}
	}

}
