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

import gov.nasa.jpf.jdwp.util.test.JdwpVerifier;
import gov.nasa.jpf.jdwp.util.test.TestJdwp;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class LineTableTest extends TestJdwp {

  public static void main(String[] args) throws SecurityException, NoSuchFieldException {
    runTestsOfThisClass(args);
  }

  /**
   * This is the reference class we query in this test set.
   * 
   * @author stepan
   * 
   */
  public static class LineTableReferenceClass {

    private String id;

    public LineTableReferenceClass() {
    }

    private String[] anArray;
    private Object[] anObjectArray = new Object[] { "hello", 3, new StringBuffer("foo") };
    private int[] anIntArray = new int[] { 1, 2, 4 };

    void referenceMethod(int i, long longish, short shortish) {
      boolean foaa = false;
      synchronized (this) {
        System.out.println("from synchronized " + foaa);
      }
      System.out.println("Hello from runnable: " + id + " ... iteration number: " + i);
      if (!Thread.currentThread().getName().contains("second")) {
        System.out.println("PROBABLY IN FIRST THREAD");
      } else {
        System.out.println("PROBABLY IN SECOND THREAD");
      }
      StringBuffer sb = new StringBuffer();
      System.out.println(sb);
      for (String string : anArray) {
        System.out.println("Array string: " + string);
        System.out.println(sb);
      }
      System.out.println("end");
    }

    public native String nativeMethod(Object param);
  }

  JdwpVerifier verifierAllLines = new JdwpVerifier() {

    @Override
    protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {

      // Prepare arguments
      ElementInfo classInstance = (ElementInfo) passedObjects[0];

      ClassInfo clazz = classInstance.getClassInfo();

      MethodInfo method = clazz.getMethod("referenceMethod(IJS)V", false);

      LineTable lt = new LineTable(method);

      lt.write(dataOutputStream);

      // verify the results
      ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());

      /*
       * Test that the header contains appropriate values
       */
      assertEquals(0, bb.getLong());
      bb.getLong();
      assertEquals(17, bb.getInt());

      Map<Integer, Long> lineToCodeIndexMap = new HashMap<Integer, Long>();
      for (int i = 0; i < 17; ++i) {
        long codeIndex = bb.getLong();
        int line = bb.getInt();
        lineToCodeIndexMap.put(line, codeIndex);
      }

      /*
       * Test that for every instruction's line we have an entry in the table
       */
      for (Instruction instruction : method.getInstructions()) {
        assertNotNull(lineToCodeIndexMap.get(instruction.getLineNumber()));
      }
    }

  };

  @Test
  public void everyLineHasEntryTest() throws SecurityException, NoSuchFieldException {
    if (verifyNoPropertyViolation()) {

      LineTableReferenceClass arrayObject = new LineTableReferenceClass();
      verifierAllLines.verify((Object) arrayObject);

      // to prevent GC of the object
      System.out.println(arrayObject);

    }
  }

  JdwpVerifier verifierUpAndDown = new JdwpVerifier() {

    @Override
    protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {
      
     
      // Prepare arguments
      ElementInfo classInstance = (ElementInfo) passedObjects[0];

      ClassInfo clazz = classInstance.getClassInfo();

      MethodInfo method = clazz.getMethod("referenceMethod(IJS)V", false);
      
      final int lo = method.getFirstInsn().getLineNumber() + 3;

      final Integer[] linesSequence = new Integer[] { lo - 3, lo - 2, lo - 1, lo - 2, lo + 1, lo + 2, lo + 3, lo + 4, lo + 5, lo + 7, lo + 8,
          lo + 9, lo + 10, lo + 11, lo + 9, lo + 13 };


      LineTable lt = new LineTable(method);

      lt.write(dataOutputStream);

      // verify the results
      ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());

      /*
       * Test that the header contains appropriate values
       */
      assertEquals(0, bb.getLong());
      long highest = bb.getLong();
      assertEquals(17, bb.getInt());

      long lastCodeIndex = 0;

      /*
       * Test that line numbers go up and down the same way as it is for
       * standard VMs
       */
      for (int line : linesSequence) {
        System.out.println("Looking for line: " + line);

        long lineCodeIndex = bb.getLong();

        assertTrue(lineCodeIndex >= 0);
        assertTrue(lineCodeIndex <= highest);
        assertTrue(lineCodeIndex >= lastCodeIndex);

        lastCodeIndex = lineCodeIndex;

        assertEquals(line, bb.getInt());
      }
    }

  };

  @Test
  public void linesGoUpAndDownTest() throws SecurityException, NoSuchFieldException {
    if (verifyNoPropertyViolation()) {

      LineTableReferenceClass arrayObject = new LineTableReferenceClass();
      verifierUpAndDown.verify((Object) arrayObject);

      // to prevent GC of the object
      System.out.println(arrayObject);

    }
  }

  JdwpVerifier verifierNativeMethod = new JdwpVerifier() {

    @Override
    protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {

      // Prepare arguments
      ElementInfo classInstance = (ElementInfo) passedObjects[0];

      ClassInfo clazz = classInstance.getClassInfo();

      MethodInfo method = clazz.getMethod("nativeMethod(Ljava/lang/Object;)Ljava/lang/String;", false);
      LineTable lt = new LineTable(method);
      lt.write(dataOutputStream);

      // verify the results
      ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());

      /*
       * Test that native method has -1
       */
      assertEquals(-1, bb.getLong());
      assertEquals(-1, bb.getLong());
      assertEquals(0, bb.getInt());
    }

  };

  @Test
  public void nativeMethodTest() throws SecurityException, NoSuchFieldException {
    if (verifyNoPropertyViolation()) {

      LineTableReferenceClass arrayObject = new LineTableReferenceClass();
      verifierNativeMethod.verify((Object) arrayObject);

      // to prevent GC of the object
      System.out.println(arrayObject);

    }
  }
}
