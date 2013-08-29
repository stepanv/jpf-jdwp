package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.id.object.ObjectId;
import gov.nasa.jpf.jdwp.id.type.ReferenceTypeId;
import gov.nasa.jpf.jdwp.util.test.CommandVerifier;
import gov.nasa.jpf.jdwp.util.test.CommandVerifier.ObjectWrapper;
import gov.nasa.jpf.jdwp.util.test.JdwpVerifier;
import gov.nasa.jpf.jdwp.util.test.TestJdwp;
import gov.nasa.jpf.jdwp.value.JdwpString;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ReferenceTypeCommandTest extends TestJdwp {

  public static void main(String[] args) throws SecurityException, NoSuchFieldException {
    runTestsOfThisClass(args);
  }

	static class ReferenceClassSuper {
		private Object privateSuperField;
	}
  /**
   * This is the reference class we query in this test set.
   * 
   * @author stepan
   * 
   */
  static class ReferenceClass {
    void method() {
    }
		
		private Object privateField;
		static String staticStringField;

    public String methodStrint(String string) {
      return "";
    }
  }

  JdwpVerifier verifierNumber1 = new JdwpVerifier() {

    @Override
    protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {

      // Prepare arguments
      ClassInfo classInfo = ClassInfo.getInitializedClassInfo("gov.nasa.jpf.jdwp.command.ReferenceTypeCommandTest$ReferenceClass",
                                                              ThreadInfo.getCurrentThread());

      // run the JDWP command
      ReferenceTypeCommand.METHODS.execute(classInfo, null, dataOutputStream, contextProvider);

      // verify the results
      ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());

      MethodInfo methodExpected = classInfo.getReflectionMethod("<init>", false);

      // assert all results
      assertEquals(3, bb.getInt());

      MethodInfo methodActual = VirtualMachineHelper.getClassMethod(classInfo, bb.getLong());

      assertEquals(methodExpected, methodActual);
      assertEquals(methodExpected.getName(), JdwpString.read(bb));
      assertEquals(methodExpected.getSignature(), JdwpString.read(bb));
      assertEquals(methodExpected.getModifiers(), bb.getInt());

    }

  };

  @Test
  public void methodsTest() throws SecurityException, NoSuchFieldException {

    // This is just an example how to debug the SuT code
    // Just put a breakpoint down there somewhere and check the console
    // where to attach the debugger (port 8000 if using defaults from
    // jpf.properties file)
    if (verifyNoPropertyViolation(/* "+listener=.jdwp.JDWPListener" */)) {

      // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      // !!! This code is run in SuT !!!
      // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

      // let the classloader load the reference class so that we can query
      // it
      System.out.println(gov.nasa.jpf.jdwp.command.ReferenceTypeCommandTest.ReferenceClass.class);

      // here we want to get the notification to the listener by running
      // the verifier's 'verify' method (which is empty in SuT)
      verifierNumber1.verify();

    }
  }
	
  private abstract static class ReferenceTypeCommandVerifier extends CommandVerifier {

		public ReferenceTypeCommandVerifier(Command command) {
			super(command);
		}
		
		protected abstract void postprepareInput(DataOutputStream inputDataOutputStream) throws IOException;
		
    @Override
		final protected void prepareInput(DataOutputStream inputDataOutputStream) throws IOException {
      ObjectId objectId = loadObjectId(0);
      ReferenceTypeId referenceTypeId = contextProvider.getObjectManager().getReferenceTypeId(objectId.get().getClassInfo());
      referenceTypeId.write(inputDataOutputStream);
			
			postprepareInput(inputDataOutputStream);
		}
		
	}

	CommandVerifier instancesVerifier = new ReferenceTypeCommandVerifier(ReferenceTypeCommand.INSTANCES) {

		@Override
		protected void postprepareInput(DataOutputStream inputDataOutputStream) throws IOException {
      inputDataOutputStream.writeInt(loadBoxObject(1, Integer.class));
    }

    @Override
    protected void processOutput(ByteBuffer outputBytes) {
      int foundInstancesCount = outputBytes.getInt();
      storeToWrapper(2, mjiEnv.newInteger(foundInstancesCount));

      for (int i = 0; i < foundInstancesCount; ++i) {
        outputBytes.get(); // we don't care about the tag byte
        ObjectId instanceObjectId = contextProvider.getObjectManager().readObjectId(outputBytes);

        storeToArray(3, i, instanceObjectId.get().getObjectRef());
      }
    }
  };

  /**
   * Complex test of all instances.
   * <ol>
   * <li>Standard test</li>
   * <li>Limited number of returned instances test</li>
   * <li>Number of instances after GC removes several of them</li>
   * </ol>
   * 
   * @throws SecurityException
   * @throws NoSuchFieldException
   */
  @Test
  public void instancesTest() throws SecurityException, NoSuchFieldException {
    if (verifyNoPropertyViolation(/* "+listener=.jdwp.JDWPListener" */)) {

      ReferenceClass testedObject = new ReferenceClass(); // +

      // JPF runs GC at the end of the method and thus if compiler let
      // these objects create it should be fine
      Object[] arrayObject1 = new Object[] { "arrayObject1", null, "foo", new ReferenceClass() }; // +
      Object[] arrayObject2 = new Object[] { "arrayObject2", new ReferenceClass(), new Object(), new ReferenceClass(), "test", testedObject }; // +
      // +
      @SuppressWarnings("unused")
      Object[] arrayObject3 = new Object[] { "arrayObject3", null, "test", new Object() };

      Object[] foundInstances = new Object[5];
      ObjectWrapper<Integer> foundInstancesNumber = new ObjectWrapper<Integer>();

      instancesVerifier.verify(testedObject, Integer.valueOf(foundInstances.length), foundInstancesNumber, foundInstances);

      assertEquals(4, foundInstancesNumber.wrappedObject.intValue());
      List<Object> foundInstancesList = Arrays.asList(foundInstances);

      // test that returned objects are really correct
      assertTrue(foundInstancesList.contains(testedObject));
      assertTrue(foundInstancesList.contains(arrayObject1[3]));
      assertTrue(foundInstancesList.contains(arrayObject2[1]));
      assertTrue(foundInstancesList.contains(arrayObject2[3]));

      // Test the number of returned instances if it is limited too
      instancesVerifier.verify(testedObject, Integer.valueOf(2), foundInstancesNumber, foundInstances);

      assertEquals(2, foundInstancesNumber.wrappedObject.intValue());

      // cleanup so that GC will remove several instances held from arrayObject2
      foundInstancesList = null;
      foundInstances = new Object[5];
      foundInstancesNumber = new ObjectWrapper<Integer>();
      arrayObject2 = null;

      // now we need to force JPF to run GC
      List<WeakReference<Object>> gcEnforcerList = new ArrayList<WeakReference<Object>>(1);
      gcEnforcerList.add(0, new WeakReference<Object>(new Object()));
      while (gcEnforcerList.get(0).get() != null) {
        gcEnforcerList.add(new WeakReference<Object>(new Object()));
      }

      // Test the number of returned instances
      instancesVerifier.verify(testedObject, Integer.valueOf(foundInstances.length), foundInstancesNumber, foundInstances);

      assertEquals(2, foundInstancesNumber.wrappedObject.intValue());
		}
	}
	
	CommandVerifier fieldsVerifier = new ReferenceTypeCommandVerifier(ReferenceTypeCommand.FIELDS) {

		@Override
		protected void postprepareInput(DataOutputStream inputDataOutputStream) throws IOException {
			// empty on a purpose
		}

		@Override
		protected void processOutput(ByteBuffer outputBytes) {
			int declared = outputBytes.getInt();
			storeToWrapper(1, mjiEnv.newInteger(declared));

			int outputArrayIndex = 0;
			
			for (int i = 0; i < declared; ++i) {
				// fieldId is thrown away
				contextProvider.getObjectManager().readFieldId(outputBytes);
				
				storeToArray(2, outputArrayIndex++, mjiEnv.newString(JdwpString.read(outputBytes)));
				storeToArray(2, outputArrayIndex++, mjiEnv.newString(JdwpString.read(outputBytes)));
				storeToArray(2, outputArrayIndex++, mjiEnv.newInteger(outputBytes.getInt()));
			}
		}
	};
	
	@Test
	public void fieldsTest() throws SecurityException, NoSuchFieldException {
		if (verifyNoPropertyViolation(/* "+listener=.jdwp.JDWPListener" */)) {

			ReferenceClass testedObject = new ReferenceClass();

			Object[] foundFields = new Object[50];
			ObjectWrapper<Integer> declared = new ObjectWrapper<Integer>();

			instancesVerifier.verify(testedObject, declared, foundFields);
			
			assertEquals(2, declared.wrappedObject.intValue());
			assertEquals("privateField", foundFields[0]);
			assertEquals("staticStringField", foundFields[3]);
			
    }
  }
}
