package gov.nasa.jpf.jdwp.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;

/**
 * Utility class for creation Line Table for a method according to the JDWP
 * Specification.
 * 
 * @author stepan
 * 
 */
public class LineTable {

	/**
	 * Lowest valid code index for the method, >=0, or -1 if the method is
	 * native
	 */
	private long start = Long.MAX_VALUE;

	/**
	 * Highest valid code index for the method, >=0, or -1 if the method is
	 * native
	 */
	private long end = -1;

	private List<LineTableItem> lineRows = new ArrayList<LineTable.LineTableItem>();

	private class LineTableItem {
		/**
		 * Initial code index of the line, start <= lineCodeIndex < end
		 */
		long lineCodeIndex;

		/**
		 * Line number
		 */
		int lineNumber;

		/**
		 * Creates the item for the {@link LineTable} which gets automatically
		 * updated.
		 * 
		 * @param instruction
		 */
		public LineTableItem(Instruction instruction) {
			lineCodeIndex = instruction.getInstructionIndex();
			lineNumber = instruction.getLineNumber();

			start = lineCodeIndex < start ? lineCodeIndex : start;
			end = lineNumber > end ? lineNumber : end;

			System.out.println("LINE TABLE: index: " + lineCodeIndex + " line: " + lineNumber + " position: " + instruction.getPosition()
					+ " ... instruction: " + instruction);
		}

		public void write(DataOutputStream os) throws IOException {
			os.writeLong(lineCodeIndex);
			os.writeInt(lineNumber);
		}

	}

	public LineTable(MethodInfo methodInfo) {
		if (methodInfo.isNative()) {
			start = -1;
			end = -1;

			// TODO I'd like to test this first
			// we're supposed to send 0 number of lines, right?
			throw new RuntimeException("HAS TO BE TESTED");
		} else {
			if (methodInfo.getInstructions() != null) {
				for (Instruction instruction : methodInfo.getInstructions()) {
					lineRows.add(new LineTableItem(instruction));
				}
			}
		}
	}

	public void write(DataOutputStream os) throws IOException {
		os.writeLong(start);
		os.writeLong(end);

		// The number of entries in the line table for this method.
		os.writeInt(lineRows.size());

		for (LineTableItem lineRow : lineRows) {
			lineRow.write(os);
		}
	}
}
