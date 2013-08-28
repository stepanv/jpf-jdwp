package gov.nasa.jpf.jdwp.util;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for creation Line Table for a method according to the JDWP
 * Specification.
 * 
 * <h3>NOTES</h3> The specification isn't really clear in the terms how the line
 * table should be constructed.<br/>
 * There is one questionable thing:<br/>
 * When a method has a <tt>for</tt> cycle for instance, the line information at
 * the end of the cycle is lower then it was with previous entries.<br/>
 * Looking at OpenJDK implementation it's obvious that whenever line number for
 * a code index sequence is changed, new line entry has to be added to the line
 * table.<br/>
 * 
 * @author stepan
 * 
 */
public class LineTable {

  final static Logger logger = LoggerFactory.getLogger(LineTable.class);

  /**
   * Lowest valid code index for the method, >=0, or -1 if the method is native
   */
  private long start = Long.MAX_VALUE;

  /**
   * Highest valid code index for the method, >=0, or -1 if the method is native
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
      end = lineCodeIndex > end ? lineCodeIndex : end;

      lineRows.add(this);

      logger.debug("LINE TABLE: index: {} line: {} position: {} ... instruction: {}", lineCodeIndex, lineNumber, instruction.getPosition(),
                   instruction);
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
    } else {
      if (methodInfo.getInstructions() != null) {
        int lastLineEntry = -1;
        for (Instruction instruction : methodInfo.getInstructions()) {
          if (lastLineEntry != instruction.getLineNumber()) {
            lastLineEntry = instruction.getLineNumber();
            new LineTableItem(instruction);
          }
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
