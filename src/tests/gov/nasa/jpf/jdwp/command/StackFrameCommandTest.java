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

import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.util.test.JdwpVerifier;
import gov.nasa.jpf.jdwp.util.test.TestJdwp;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import org.junit.Test;

/**
 * Tests of StackFrame command.
 * 
 * @author stepan
 * 
 */
public class StackFrameCommandTest extends TestJdwp {

  public static void main(String[] args) throws SecurityException, NoSuchFieldException {
    runTestsOfThisClass(args);
  }

  JdwpVerifier verifierSetValues = new JdwpVerifier() {

    @Override
    protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {

      // Prepare arguments
      bytes.putInt(11); // changing 6 vars

      ElementInfo stringElementInfo = VM.getVM().getHeap().newString("ModifiedStringValue2", ThreadInfo.getCurrentThread());
      // we need to create an association for this string instance in the
      // Object ID Manager
      JdwpObjectManager.getInstance().getObjectId(stringElementInfo);
      ElementInfo stringArgElementInfo = VM.getVM().getHeap().newString("argModified", ThreadInfo.getCurrentThread());
      // we need to create an association for this string instance in the
      // Object ID Manager
      JdwpObjectManager.getInstance().getObjectId(stringArgElementInfo);

      // 0: this

      bytes.putInt(1);
      bytes.put(Tag.OBJECT.identifier());
      bytes.putLong(stringArgElementInfo.getObjectRef());

      // 2: object reference unmodified

      bytes.putInt(3);
      bytes.put(Tag.LONG.identifier());
      bytes.putLong(100L);

      // 4: argLongModified is low (already modified)

      // 5: argLongUnmodified high
      // 6: argLongUnmodified low

      int localBase = 6;

      bytes.putInt(localBase + 1);
      bytes.put(Tag.INT.identifier());
      bytes.putInt(10);

      // localBase+2: unmodifiedInt keeps -19

      bytes.putInt(localBase + 3);
      bytes.put(Tag.LONG.identifier());
      bytes.putLong(3000000000000L);

      // localBase+4: modifiedLong is low (already modified)

      // localBase+5: unmodifiedLong high
      // localBase+6: unmodifiedLong low
      // localBase+7: localNull stays as null

      bytes.putInt(localBase + 8);
      bytes.put(Tag.OBJECT.identifier());
      bytes.putLong(stringElementInfo.getObjectRef());

      // localBase+9: unmodifiedString stays unmodified

      bytes.putInt(localBase + 10);
      bytes.put(Tag.OBJECT.identifier());
      bytes.putLong(0);

      bytes.putInt(localBase + 11);
      bytes.put(Tag.DOUBLE.identifier());
      bytes.putDouble(5.3E35);

      // localBase+12: modifiedDouble is low (already modified)

      // localBase+13: unmodifiedDouble high stays unmodified
      // localBase+14: unmodifiedDouble low stays unmodified

      bytes.putInt(localBase + 15);
      bytes.put(Tag.SHORT.identifier());
      bytes.putShort((short) 2);

      // localBase+16: unmodifiedShort stays unmodified

      bytes.putInt(localBase + 17);
      bytes.put(Tag.FLOAT.identifier());
      bytes.putFloat(83.8E2F);

      // localBase+18: unmodifiedFloat stays unmodified

      bytes.putInt(localBase + 19);
      bytes.put(Tag.BYTE.identifier());
      bytes.put((byte) 3);

      // localBase+20: unmodifiedByte stays unmodified

      bytes.putInt(localBase + 21);
      bytes.put(Tag.CHAR.identifier());
      bytes.putChar('3');

      // localBase+21: unmodifiedChar stays unmodified

      bytes.rewind();

      ThreadInfo currentThread = ThreadInfo.getCurrentThread();

      // run the JDWP command

      StackFrameCommand.SETVALUES.execute(currentThread, currentThread.getTopFrame().getPrevious(), bytes, dataOutputStream,
                                          contextProvider);

      // results are verified in SuT
    }

  };

  public void setValuesInnerMethod(Object argObjectModified, Object argObjectUnmodified, long argLongModified, long argLongUnmodified) {
    int modifiedInt = 8;
    int unmodifiedInt = -19;
    long modifiedLong = 1000000000000L;
    long unmodifiedLong = 2000000000000L;
    Object localNull = null;
    Object modifiedString = "hello";
    Object unmodifiedString = "thesame";
    String[] localArray = new String[] { "1", null, "2" };
    double modifiedDouble = 3.5E36;
    double unmodifiedDouble = 3.4E34;
    short modifiedShort = (short) 3;
    short unmodifiedShort = (short) 45;
    float modifiedFloat = 2.3E18F;
    float unmodifiedFloat = 2.3E2F;
    byte modifiedByte = 1;
    byte unmodifiedByte = 2;
    char modifiedChar = '1';
    char unmodifiedChar = '2';
    Object lastObject = "last";

    verifierSetValues.verify();

    assertEquals("argModified", argObjectModified);
    assertEquals("paramStringUnmodified", argObjectUnmodified);
    assertEquals(100L, argLongModified);
    assertEquals(6L, argLongUnmodified);
    assertEquals(10, modifiedInt);
    assertEquals(-19, unmodifiedInt);
    assertEquals(3000000000000L, modifiedLong);
    assertEquals(2000000000000L, unmodifiedLong);
    assertEquals(null, localNull);
    assertEquals("ModifiedStringValue2", modifiedString);
    assertEquals("thesame", unmodifiedString);
    assertEquals(null, localArray);
    assertEquals(5.3E35, modifiedDouble);
    assertEquals(3.4E34, unmodifiedDouble);
    assertEquals((short) 2, modifiedShort);
    assertEquals((short) 45, unmodifiedShort);
    assertEquals(83.8E2F, modifiedFloat);
    assertEquals(2.3E2F, unmodifiedFloat);
    assertEquals(3, modifiedByte);
    assertEquals(2, unmodifiedByte);
    assertEquals('3', modifiedChar);
    assertEquals('2', unmodifiedChar);

    assertEquals("last", lastObject);
  }

  @Test
  public void setValuesTest() throws SecurityException, NoSuchFieldException {
    if (verifyNoPropertyViolation()) {
      setValuesInnerMethod("paramString", "paramStringUnmodified", 5L, 6L);
    }
  }

}