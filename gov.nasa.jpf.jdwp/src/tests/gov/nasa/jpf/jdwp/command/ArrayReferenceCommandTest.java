package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.util.test.JdwpVerifier;
import gov.nasa.jpf.jdwp.util.test.TestJdwp;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import java.nio.ByteBuffer;

import org.junit.Test;

public class ArrayReferenceCommandTest extends TestJdwp {

	
	public static void main(String[] args) throws SecurityException, NoSuchFieldException {
		runTestsOfThisClass(args);
	}

	/**
	 * This is the reference class we query in this test set.
	 * 
	 * @author stepan
	 * 
	 */
	public static class ArrayTestReferenceClass {
		static Object[] staticArray = new Object[] {"StringValue1", null, new Integer(1)};
		Object[] instanceArray = new Object[] {null, "unused", "StringValue2", null, null, new Integer(2), "unused again", null};
		
		void method() {
			Object[] variableArray = new Object[] {"StringValue3", null, null, new Integer(3), null};
		}
	}

	JdwpVerifier verifierNumber1 = new JdwpVerifier() {

		@Override
		protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {

			// Prepare arguments
			ElementInfo array = (ElementInfo) passedObjects[0];
			// run the JDWP command
			ArrayReferenceCommand.LENGTH.execute(array, null, dataOutputStream, contextProvider);

			// verify the results
			ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());

			// assert all results
			assertEquals(8, bb.getInt());
			
			reset();
			
			// Prepare arguments
			array = (ElementInfo) passedObjects[1];
			// run the JDWP command
			ArrayReferenceCommand.LENGTH.execute(array, null, dataOutputStream, contextProvider);

			// verify the results
			bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());

			// assert all results
			assertEquals(3, bb.getInt());
		}

	};

	@Test
	public void lenghtTest() throws SecurityException, NoSuchFieldException {
		if (verifyNoPropertyViolation()) {
			
			ArrayTestReferenceClass arrayObject = new ArrayTestReferenceClass();
			verifierNumber1.verify((Object)arrayObject.instanceArray, ArrayTestReferenceClass.staticArray);

			// to prevent GC of the object
			System.out.println(arrayObject);

		}
	}
	
	JdwpVerifier verifierNumber2 = new JdwpVerifier() {

		@Override
		protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {

			// Prepare arguments
			ElementInfo array = (ElementInfo) passedObjects[0];
			bytes.putInt(2);
			bytes.putInt(4);
			bytes.rewind();
			
			// run the JDWP command
			ArrayReferenceCommand.GETVALUES.execute(array, bytes, dataOutputStream, contextProvider);

			// verify the results
			ByteBuffer outputBytes = ByteBuffer.wrap(dataOutputBytes.toByteArray());

			assertEquals('L', outputBytes.get());
			assertEquals(4, outputBytes.getInt());
			
			assertEquals('s', outputBytes.get());
			assertEquals(array.getReferenceElement(2), outputBytes.getLong());
			
			assertEquals('L', outputBytes.get());
			assertEquals(0, outputBytes.getLong());
			
			assertEquals('L', outputBytes.get());
			assertEquals(0, outputBytes.getLong());
			
			assertEquals('L', outputBytes.get());
			assertEquals(array.getReferenceElement(5), outputBytes.getLong());
		}

	};

	@Test
	public void getValuesTest() throws SecurityException, NoSuchFieldException {
		if (verifyNoPropertyViolation()) {
			
			ArrayTestReferenceClass arrayObject = new ArrayTestReferenceClass();
			verifierNumber2.verify((Object)arrayObject.instanceArray);

			// to prevent GC of the object
			System.out.println(arrayObject);

		}
	}
	
	JdwpVerifier verifierNumber3 = new JdwpVerifier() {

		@Override
		protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {

			// Prepare arguments
			ElementInfo array = (ElementInfo) passedObjects[0];
			bytes.putInt(2);
			bytes.putInt(2);
			
			ElementInfo stringElementInfo = VM.getVM().getHeap().newString("ModifiedStringValue2", ThreadInfo.getCurrentThread());
			// we need to create an association for this string instance in the Object ID Manager
			JdwpObjectManager.getInstance().getObjectId(stringElementInfo);
			
			bytes.putLong(0);
			bytes.putLong(stringElementInfo.getObjectRef());
			bytes.rewind();
			
			// run the JDWP command
			ArrayReferenceCommand.SETVALUES.execute(array, bytes, dataOutputStream, contextProvider);

			// results are verified in SuT
		}

	};

	@Test
	public void setValuesTest() throws SecurityException, NoSuchFieldException {
		if (verifyNoPropertyViolation()) {
			
			ArrayTestReferenceClass arrayObject = new ArrayTestReferenceClass();
			
			assertEquals(arrayObject.instanceArray[1], "unused");
			assertEquals(arrayObject.instanceArray[2], "StringValue2");
			assertEquals(arrayObject.instanceArray[3], null);
			assertEquals(arrayObject.instanceArray[4], null);
			
			verifierNumber3.verify((Object)arrayObject.instanceArray);
			
			assertEquals(arrayObject.instanceArray[1], "unused");
			assertEquals(arrayObject.instanceArray[2], null);
			assertEquals(arrayObject.instanceArray[3], "ModifiedStringValue2");
			assertEquals(arrayObject.instanceArray[4], null);

			// to prevent GC of the object
			System.out.println(arrayObject);

		}
	}
	
}