package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jdwp.VirtualMachine;
import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.util.test.CommandVerifier;
import gov.nasa.jpf.jdwp.util.test.CommandVerifier.ObjectWrapper;
import gov.nasa.jpf.jdwp.util.test.TestJdwp;
import gov.nasa.jpf.vm.VM;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Junit test class that tests commands of Object Reference command set.
 * 
 * @author stepan
 * 
 */
public class ObjectReferenceCommandTest extends TestJdwp {

	public static void main(String[] testMethods) {
		runTestsOfThisClass(testMethods);
	}

	/**
	 * This is the reference class we query in this test set.
	 * 
	 * @author stepan
	 * 
	 */
	public static class ObjectReferenceTestReferenceClass {
		Object instanceObject = new Object();
		String instanceString = "test";
	}

	CommandVerifier objectReferenceVerifier = new CommandVerifier(ObjectReferenceCommand.SETVALUES) {

		@Override
		protected void prepareInput(DataOutputStream inputDataOutputStream) throws IOException {
			ObjectId objectId = loadObjectId(0);
			objectId.write(inputDataOutputStream);
			
			inputDataOutputStream.writeInt(1);
			toFieldId(objectId, 1).write(inputDataOutputStream);
			loadObjectId(2).writeUntagged(inputDataOutputStream);
		}

		@Override
		protected void processOutput(ByteBuffer outputBytes) {
			// empty on a purpose

		}
	};

	Logger logger = LoggerFactory.getLogger(ObjectReferenceTestReferenceClass.class);

	@Test
	public void setValuesTest() throws SecurityException, NoSuchFieldException {
		if (verifyNoPropertyViolation(/* "+listener=.jdwp.JDWPListener" */)) {
			// prepare and clear before the test
			ObjectReferenceTestReferenceClass objectRefClass = new ObjectReferenceTestReferenceClass();
			String string = "testaaaa";

			objectReferenceVerifier.verify(objectRefClass, "instanceObject", string);

			assertEquals("testaaaa", objectRefClass.instanceObject);
		}
	}

	CommandVerifier referringObjectsVerifier = new CommandVerifier(ObjectReferenceCommand.REFERRINGOBJECTS) {

		@Override
		protected void prepareInput(DataOutputStream inputDataOutputStream) throws IOException {
			loadObjectId(0).write(inputDataOutputStream);
			inputDataOutputStream.writeInt(loadBoxObject(1, Integer.class));
		}

		@Override
		protected void processOutput(ByteBuffer outputBytes) {
			int foundReferringObjectNumber = outputBytes.getInt();

			storeToWrapper(2, mjiEnv.newInteger(foundReferringObjectNumber));

			for (int i = 0; i < foundReferringObjectNumber; ++i) {
				outputBytes.get(); // we don't care about the tag byte
				ObjectId referringObjectId = contextProvider.getObjectManager().readObjectId(outputBytes);
				
				storeToArray(3, i, referringObjectId.get().getObjectRef());
			}
		}
	};

	/**
	 * Basic test of referees.
	 * 
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	@Test
	public void referringObjectsTest() throws SecurityException, NoSuchFieldException {
		if (verifyNoPropertyViolation(/* "+listener=.jdwp.JDWPListener" */)) {

			// we're testing mutable object .. immutable are weird
			Object testedObject = new Object();

			// prepare and clear before the test
			ObjectReferenceTestReferenceClass objectRefClass = new ObjectReferenceTestReferenceClass();
			objectRefClass.instanceObject = testedObject;

			// JPF runs GC at the end of the method and thus of compiler let
			// these objects create it should be fine
			@SuppressWarnings("unused")
			Object[] arrayObject1 = new Object[] { "arrayObject1", null, "foo", new Object() };
			Object[] arrayObject2 = new Object[] { "arrayObject2", null, "foo", new Object(), testedObject, "test", testedObject };
			@SuppressWarnings("unused")
			Object[] arrayObject3 = new Object[] { "arrayObject3", null, "test", new Object() };

			Object[] foundReferringObjects = new Object[5];
			ObjectWrapper<Integer> foundReferringObjectNumber = new ObjectWrapper<Integer>();

			// for convenience reasons, we're passing arguments as an single
			// array, because this array will be created anyway and as such it
			// will refer to the testedObject too
			Object[] args = new Object[] { testedObject, Integer.valueOf(foundReferringObjects.length), foundReferringObjectNumber, foundReferringObjects };
			referringObjectsVerifier.verify(args);

			assertEquals(3, foundReferringObjectNumber.wrappedObject.intValue());
			List<Object> returnedReferringObjectList = Arrays.asList(foundReferringObjects);

			// test that returned objects are really correct
			assertTrue(returnedReferringObjectList.contains(arrayObject2));
			assertTrue(returnedReferringObjectList.contains(args));
			assertTrue(returnedReferringObjectList.contains(objectRefClass));
		}
	}

	/**
	 * Tests whether correct number of referees is returned if it is required
	 * less than how many there really is.
	 * 
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	@Test
	public void referringObjectsLimitedTest() throws SecurityException, NoSuchFieldException {
		if (verifyNoPropertyViolation(/* "+listener=.jdwp.JDWPListener" */)) {
			// we're testing mutable object .. immutable are weird
			Object testedObject = new Object();

			// prepare and clear before the test
			ObjectReferenceTestReferenceClass objectRefClass = new ObjectReferenceTestReferenceClass();
			objectRefClass.instanceObject = testedObject;

			// JPF runs GC at the end of the method and thus of compiler let
			// these objects create it should be fine
			@SuppressWarnings("unused")
			Object[] arrayObject1 = new Object[] { "arrayObject1", null, "foo", new Object() };
			@SuppressWarnings("unused")
			Object[] arrayObject2 = new Object[] { "arrayObject2", null, "foo", new Object(), testedObject, "test", testedObject };
			@SuppressWarnings("unused")
			Object[] arrayObject3 = new Object[] { "arrayObject3", null, "test", new Object() };

			Object[] foundReferringObjects = new Object[2];
			ObjectWrapper<Integer> foundReferringObjectNumber = new ObjectWrapper<Integer>();

			// for convenience reasons, we're passing arguments as an single
			// array, because this array will be created anyway and as such it
			// will refer to the testedObject too
			Object[] args = new Object[] { testedObject, Integer.valueOf(foundReferringObjects.length), foundReferringObjectNumber, foundReferringObjects };
			referringObjectsVerifier.verify(args);

			assertEquals(2, foundReferringObjectNumber.wrappedObject.intValue());

			// we don't need to test which objects were returned since even the
			// JDWP spec doesn't determine which should be returned
		}
	}

	/**
	 * Tests whether the number of referees is correct if GC is performed.
	 * 
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	@Test
	public void referringObjectsAfterGCTest() throws SecurityException, NoSuchFieldException {
		if (verifyNoPropertyViolation(/* "+listener=.jdwp.JDWPListener" */)) {

			// we're testing mutable object .. immutable are weird
			Object testedObject = new Object();

			// prepare and clear before the test
			ObjectReferenceTestReferenceClass objectRefClass = new ObjectReferenceTestReferenceClass();
			objectRefClass.instanceObject = testedObject;

			Object[] foundReferringObjects = new Object[5];
			ObjectWrapper<Integer> foundReferringObjectNumber = new ObjectWrapper<Integer>();

			// for convenience reasons, we're passing arguments as an single
			// array, because this array will be created anyway and as such it
			// will refer to the testedObject too
			Object[] args = new Object[] { testedObject, Integer.valueOf(foundReferringObjects.length), foundReferringObjectNumber, foundReferringObjects };

			ObjectWrapper<WeakReference<Object>> wrapper = new ObjectWrapper<WeakReference<Object>>();
			createWeaklyReferencedObject(wrapper, testedObject);

			referringObjectsVerifier.verify(args);

			List<Object> returnedReferringObjectList = Arrays.asList(foundReferringObjects);

			// test weak reference object in a separate method
			assertResultsBeforeGC(wrapper, foundReferringObjectNumber, returnedReferringObjectList);

			// test the rest of the returned objects
			assertTrue(returnedReferringObjectList.contains(args));
			assertTrue(returnedReferringObjectList.contains(objectRefClass));

			// cleanup
			foundReferringObjects = new Object[5];
			args[3] = foundReferringObjects;
			returnedReferringObjectList = null;

			// now we need to force JPF to run GC
			List<WeakReference<Object>> gcEnforcerList = new ArrayList<WeakReference<Object>>(1);
			gcEnforcerList.add(0, new WeakReference<Object>(new Object()));
			while (gcEnforcerList.get(0).get() != null) {
				gcEnforcerList.add(new WeakReference<Object>(new Object()));
			}

			// an assertion whether the desired object was really cleared
			// this is assertion is rather for programmers than for the test
			// itself
			assertTrue(wrapper.wrappedObject.get() == null);

			referringObjectsVerifier.verify(args);

			assertEquals(2, foundReferringObjectNumber.wrappedObject.intValue());
			returnedReferringObjectList = Arrays.asList(foundReferringObjects);

			// test that returned objects are really correct
			assertTrue(returnedReferringObjectList.contains(args));
			assertTrue(returnedReferringObjectList.contains(objectRefClass));
		}
	}

	/**
	 * Assert the results of the referring object that is weakly referenced.<br/>
	 * It is taken into an account that it is not known whether GC was run
	 * already.<br/>
	 * The manipulation with the possibly collected object needs to be done in a
	 * separated method because JPF doesn't perform GC otherwise.
	 * 
	 * @param wrapper
	 *            The wrapper of the weak reference
	 * @param foundReferringObjectNumber
	 *            The number of found referring objects
	 * @param returnedReferringObjectList
	 *            The list of found referring objects
	 */
	private void assertResultsBeforeGC(ObjectWrapper<WeakReference<Object>> wrapper, ObjectWrapper<Integer> foundReferringObjectNumber,
			List<Object> returnedReferringObjectList) {
		Object possiblyCollectedObject = wrapper.wrappedObject.get();

		if (possiblyCollectedObject != null) {
			assertEquals(3, foundReferringObjectNumber.wrappedObject.intValue());
			assertTrue(returnedReferringObjectList.contains(possiblyCollectedObject));
		} else {
			assertEquals(2, foundReferringObjectNumber.wrappedObject.intValue());
		}
	}

	/**
	 * For some reason JPF does GC only local variables that when a method is
	 * finished. And not when just a scope ends.
	 * 
	 * @param reference
	 */
	private void createWeaklyReferencedObject(ObjectWrapper<WeakReference<Object>> reference, Object testedObject) {
		Object[] arrayObject2 = new Object[] { "arrayObject2", null, "foo", new Object(), "test", testedObject, "test", testedObject };

		reference.wrappedObject = new WeakReference<Object>(arrayObject2);
	}

	/**
	 * Tests whether referring objects of immutable objects are returned
	 * correctly.
	 * 
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	@Test
	public void referringObjectsImmutableTest() throws SecurityException, NoSuchFieldException {
		if (verifyNoPropertyViolation(/* "+listener=.jdwp.JDWPListener" */)) {

			// Immutable objects are weird. Especially when it comes to strings.
			// :-)
			// If you use the same string "foo" in different location in your
			// code it can be the same object instance.
			// It's probably compiler who decides so.

			// prepare test data
			String foo = "foo";
			String anotherFoo = "f";
			System.out.println(anotherFoo);
			anotherFoo += "oo";
			String[] allThreeFoos = new String[] { null, "ahoj", "foo", foo, anotherFoo }; // +
			@SuppressWarnings("unused")
			Object[] noFoo = new Object[] { null, "ahoj", "nofoo", null, anotherFoo };
			@SuppressWarnings("unused")
			Object[] noFooAtAll = new String[] { null, "ahoj", "nofoo", null, "nononoFoo" };
			Object[] justRefFoos = new Object[] { null, "ahoj", foo, anotherFoo, Integer.valueOf(4) }; // +
			String[] onlyCompileFoo = new String[] { null, new String(), "foo", "bar" }; // +

			// prepare wrappers for results
			Object[] foundReferringObjects = new Object[5];
			ObjectWrapper<Integer> foundReferringObjectNumber = new ObjectWrapper<Integer>();

			// run the command with proper arguments
			Object[] args = new Object[] { foo, Integer.valueOf(foundReferringObjects.length), foundReferringObjectNumber, foundReferringObjects };
			referringObjectsVerifier.verify(args);

			// test the correct number of returned referees
			assertEquals(4, foundReferringObjectNumber.wrappedObject.intValue());

			// test that returned objects are really correct
			List<Object> returnedReferringObjectList = Arrays.asList(foundReferringObjects);
			assertTrue(returnedReferringObjectList.contains(args));
			assertTrue(returnedReferringObjectList.contains(allThreeFoos));
			assertTrue(returnedReferringObjectList.contains(justRefFoos));
			assertTrue(returnedReferringObjectList.contains(onlyCompileFoo));
		}
	}

	/**
	 * Tests whether null object triggers correct error state.
	 * 
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IOException
	 * @throws JdwpError
	 */
	@Test(expected = gov.nasa.jpf.jdwp.exception.InvalidObject.class)
	public void referringObjectsNullTest() throws SecurityException, NoSuchFieldException, IOException, JdwpError {

		String[] args = { "+target=HelloWorld" }; // using HelloWorld from
		// jpf-core src/examples
		Config config = new Config(args);
		JPF jpf = new JPF(config);
		VM vm = jpf.getVM();

		ByteArrayOutputStream dataOutputBytes = new ByteArrayOutputStream(0);
		DataOutputStream dataOutputStream = new DataOutputStream(dataOutputBytes);
		CommandContextProvider contextProvider = new CommandContextProvider(new VirtualMachine(jpf), JdwpObjectManager.getInstance());

		vm.initialize();

		ByteBuffer bytes = ByteBuffer.allocate(200);
		bytes.putLong(0);
		ObjectReferenceCommand.REFERRINGOBJECTS.execute(bytes, dataOutputStream, contextProvider);

		assertTrue("We should not reach this line of code.", false);
	}
}
