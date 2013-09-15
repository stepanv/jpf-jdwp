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

package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.util.test.BasicJdwpVerifier;
import gov.nasa.jpf.jdwp.util.test.TestJdwp;
import gov.nasa.jpf.jdwp.value.JdwpString;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * @author stepan
 *
 */
public class MethodCommandTest extends TestJdwp {

  public static class MethodCommandReferenceClass {
    public static void foo2() {
      System.out.println("ahoj");
    }
    public static int foo(int numArg, Object objArg, long longArg) {
      System.out.println(numArg);
      
      int localInt = objArg.hashCode();
      
      for (int i = 0; i < numArg; ++i) {
        int inner = (int) (longArg / i);
        localInt += inner;
      }
      
      return localInt;
    }
  }
  
  BasicJdwpVerifier variableTableVerifier = new BasicJdwpVerifier() {

    @Override
    public void test() throws Throwable {
      initializeSimpleJPF();

      ClassInfo classInfo = ClassLoaderInfo.getCurrentResolvedClassInfo(MethodCommandReferenceClass.class.getName());
      MethodInfo methodInfo = classInfo.getMethod("foo(ILjava/lang/Object;J)I", false);

      simpleJpfContextProvider().getObjectManager().getReferenceTypeId(classInfo);

      MethodCommand.VARIABLETABLE.execute(methodInfo, bytes, dataOutputStream, simpleJpfContextProvider());

      wrapTheOutput();

      Set<String> foundVars = new HashSet<>();
      
      int argCnt = outputBytes.getInt();
      int slots = outputBytes.getInt();
      
      for (int i = 0; i < slots; ++i) {
        long codeIndex = outputBytes.getLong();
        String name = JdwpString.read(outputBytes);
        String signature = JdwpString.read(outputBytes);
        int length = outputBytes.getInt();
        int slot = outputBytes.getInt();
        
        System.out.println(codeIndex + name + signature + length + slot);
        
        foundVars.add(name);
      }

      assertEquals(4, argCnt);
      assertEquals(3 + 3, slots);
      
      assertTrue(foundVars.contains("longArg"));
      assertTrue(foundVars.contains("i"));
    }

  };

  /**
   * Test the variable table command.
   */
  @Test
  public void variableTableTest() throws Throwable {
    variableTableVerifier.test();
  }
  
  BasicJdwpVerifier lineTableVerifier = new BasicJdwpVerifier() {

    @Override
    public void test() throws Throwable {
      initializeSimpleJPF();

      ClassInfo classInfo = ClassLoaderInfo.getCurrentResolvedClassInfo(MethodCommandReferenceClass.class.getName());
      MethodInfo methodInfo = classInfo.getMethod("foo(ILjava/lang/Object;J)I", false);

      simpleJpfContextProvider().getObjectManager().getReferenceTypeId(classInfo);

      MethodCommand.LINETABLE.execute(methodInfo, bytes, dataOutputStream, simpleJpfContextProvider());

      wrapTheOutput();

      long start = outputBytes.getLong();
      long end = outputBytes.getLong();
      int lines = outputBytes.getInt();
      
      for (int i = 0; i < lines; ++i) {
        long lineCodeIndex = outputBytes.getLong();
        int lineNumber = outputBytes.getInt();
        
        assertTrue(start <= lineCodeIndex);
        assertTrue(lineCodeIndex < end);  
        
        System.out.println("");
        System.out.println("Line: " + lineNumber);
        
        for (Instruction instruction : methodInfo.getInstructionsForLine(lineNumber)) {
          long insIndex = instruction.getInstructionIndex();
          System.out.println(insIndex);
          assertEquals(lineNumber, instruction.getLineNumber());
          
        }
        System.out.println(lineCodeIndex + " " + lineNumber);
      }

      assertEquals(0, start);
      assertEquals(24, end);
      assertEquals(7, lines);
    }

  };

  /**
   * Test the nested types command.
   */
  @Test
  public void lineTableTest() throws Throwable {
    lineTableVerifier.test();
  }
  
}
