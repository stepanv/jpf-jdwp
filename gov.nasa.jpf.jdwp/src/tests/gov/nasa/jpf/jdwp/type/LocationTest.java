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

package gov.nasa.jpf.jdwp.type;

import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId.TypeTag;
import gov.nasa.jpf.jdwp.util.test.JdwpVerifier;
import gov.nasa.jpf.jdwp.util.test.TestJdwp;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;

import java.nio.ByteBuffer;

import org.junit.Test;

public class LocationTest extends TestJdwp {

  public static void main(String[] args) throws SecurityException, NoSuchFieldException {
    runTestsOfThisClass(args);
  }

  public static interface LocationReferenceInterface {
    // static {
    // System.out.println("Hello from the interface static block.");
    // }
    void referenceMethod(int i, long longish, short shortish);

    public String nativeMethod(Object param);
  }

  /**
   * This is the reference class we query in this test set.
   * 
   * @author stepan
   * 
   */
  public static class LocationReferenceClass implements LocationReferenceInterface {

    public LocationReferenceClass() {
      System.out.println("Hello from the class constructor.");
    }

    static {
      System.out.println("Hello from the class static block.");
    }

    private Object[] anObjectArray = new Object[] { "hello", 3, new StringBuffer("foo") };

    public void referenceMethod(int i, long longish, short shortish) {
      System.out.println("Hello from the method");
    }

    public native String nativeMethod(Object param);
  }

  JdwpVerifier verifierOneLocation = new JdwpVerifier() {

    final int lo = 50;

    @Override
    protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {

      // Prepare arguments
      ElementInfo classInstance = (ElementInfo) passedObjects[0];

      ClassInfo clazz = classInstance.getClassInfo();

      MethodInfo method = clazz.getMethod("referenceMethod(IJS)V", false);

      Instruction instruction = method.getInstructionsForLine(lo + 1)[0];
      Location location = Location.factory(instruction);
      location.write(dataOutputStream);

      // verify the results
      ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());

      /*
       * Test that the header contains appropriate values
       */
      assertEquals(TypeTag.CLASS.identifier().byteValue(), bb.get());
      // ObjectId objectId =
      ReferenceTypeId refId = JdwpObjectManager.getInstance().readReferenceTypeId(bb);
      assertEquals(clazz, refId.get());
      assertEquals(method.getGlobalId(), bb.getLong());
      assertEquals(instruction.getInstructionIndex(), bb.getLong());
    }

  };

  @Test
  public void simpleLocationTest() throws SecurityException, NoSuchFieldException {
    if (verifyNoPropertyViolation()) {

      LocationReferenceClass locationClassObject = new LocationReferenceClass();
      verifierOneLocation.verify((Object) locationClassObject);

      // to prevent GC of the object
      System.out.println(locationClassObject);

    }
  }
}
