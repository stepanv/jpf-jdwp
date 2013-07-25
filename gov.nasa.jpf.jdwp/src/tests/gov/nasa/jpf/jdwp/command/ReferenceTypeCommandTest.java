package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.VirtualMachineHelper;
import gov.nasa.jpf.jdwp.util.test.JdwpVerifier;
import gov.nasa.jpf.jdwp.util.test.TestJdwp;
import gov.nasa.jpf.jdwp.value.JdwpString;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.nio.ByteBuffer;

import org.junit.Test;

public class ReferenceTypeCommandTest extends TestJdwp {

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
		if (!isJPFRun()) { // run this outside of JPF

			// right now only 1 verifier is supported
			// TODO get rid of this and use full reflection to find which
			// verifier to execute
			initialize(verifierNumber1);
		}

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
}
