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

import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.util.test.BasicJdwpVerifier;
import gov.nasa.jpf.jdwp.util.test.CommandVerifier;
import gov.nasa.jpf.jdwp.util.test.CommandVerifier.ObjectWrapper;
import gov.nasa.jpf.jdwp.util.test.TestJdwp;
import gov.nasa.jpf.jdwp.value.JdwpString;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class VirtualMachineCommandTest extends TestJdwp {

  public static void main(String[] args) throws SecurityException, NoSuchFieldException {
    runTestsOfThisClass(args);
  }

  /**
   * This is the reference class we query in this test set.
   * 
   * @author stepan
   * 
   */
  public static class ReferenceClass {
    void method() {

    }

    public String methodStrint(String string) {
      return "";
    }
  }

  CommandVerifier instanceCountsVerifier = new CommandVerifier(VirtualMachineCommand.INSTANCECOUNTS) {

    int index = 0;

    @Override
    protected void prepareInput(DataOutputStream inputDataOutputStream) throws IOException {
      int refTypesCount = loadBoxObject(index++, Integer.class);
      inputDataOutputStream.writeInt(refTypesCount);

      for (int i = 0; i < refTypesCount; ++i) {
        ObjectId objectId = loadObjectId(index++);
        ReferenceTypeId referenceTypeId = contextProvider.getObjectManager().getReferenceTypeId(objectId.get().getClassInfo());
        referenceTypeId.write(inputDataOutputStream);
      }
    }

    @Override
    protected void processOutput(ByteBuffer outputBytes) {
      int counts = outputBytes.getInt();
      storeToWrapper(index++, mjiEnv.newInteger(counts));

      for (int i = 0; i < counts; ++i) {
        long instanceCount = outputBytes.getLong();
        storeToArray(index, i, mjiEnv.newLong(instanceCount));
      }
    }
  };

  /**
   * Simple test of instance counts..
   * 
   * @throws SecurityException
   * @throws NoSuchFieldException
   */
  @Test
  public void instanceCountsTest() throws SecurityException, NoSuchFieldException {
    if (verifyNoPropertyViolation(/* "+listener=.jdwp.JDWPListener" */)) {

      ReferenceClass testedObject = new ReferenceClass(); // +

      // JPF runs GC at the end of the method and thus if compiler let
      // these objects create it should be fine
      @SuppressWarnings("unused")
      Object[] arrayObject1 = new Object[] { "arrayObject1", null, "foo", new ReferenceClass() }; // +
      @SuppressWarnings("unused")
      Object[] arrayObject2 = new Object[] { "arrayObject2", new ReferenceClass(), new Object(), new ReferenceClass(), "test", testedObject }; // +
      @SuppressWarnings("unused")
      Object[] arrayObject3 = new Object[] { "arrayObject3", null, "test", new Object() };

      Object[] foundInstanceCounts = new Object[5];
      ObjectWrapper<Integer> foundCounts = new ObjectWrapper<Integer>();

      instanceCountsVerifier.verify(Integer.valueOf(3), testedObject, "foo", this, foundCounts, foundInstanceCounts);

      assertEquals(3, foundCounts.wrappedObject.intValue());
      // 4 instances of ReferenceClass
      assertEquals(4, ((Long) foundInstanceCounts[0]).longValue());
      // there might be about 300 instances of String
      assertTrue(((Long) foundInstanceCounts[1]).longValue() > 12);
      // 1 instance of this class
      assertEquals(1, ((Long) foundInstanceCounts[2]).longValue());
    }
  }
  
  BasicJdwpVerifier classpathVerifier = new BasicJdwpVerifier() {

    @Override
    public void test() throws Throwable {
      
      initializeSimpleJPF();
      VirtualMachineCommand.CLASSPATHS.execute(bytes, dataOutputStream, simpleJpfContextProvider());
      wrapTheOutput();
      
      File dir = new File(JdwpString.read(outputBytes));
      assertTrue(dir.exists() && dir.isDirectory());
      
      Set<String> foundClasspath = new HashSet<>();
      int num = outputBytes.getInt();
      for (int i = 0; i < num; ++i) {
        String pathElement = JdwpString.read(outputBytes);
        foundClasspath.add(pathElement);
        System.out.println(pathElement);
        assertTrue(new File(pathElement).exists());
      }
      
    }
    
  };
  @Test
  public void classpathsTest() throws Throwable {
    classpathVerifier.test();
  }
}
