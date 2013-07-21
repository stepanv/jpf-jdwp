package gov.nasa.jpf.jdwp;

import gov.nasa.jpf.jdwp.VirtualMachineHelper.MethodResult;
import gov.nasa.jpf.jdwp.id.JdwpObjectManager;
import gov.nasa.jpf.jdwp.util.test.JdwpVerifier;
import gov.nasa.jpf.jdwp.util.test.TestJdwp;
import gov.nasa.jpf.jdwp.value.PrimitiveValue.Tag;
import gov.nasa.jpf.jdwp.value.Value;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.DynamicElementInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.VM;

import java.nio.ByteBuffer;

import org.junit.Test;

/**
 * Test class for VirtualMachine Helper class.
 * 
 * Currently, basics of method invocations are tested.
 * @author stepan
 *
 */
public class VirtualMachineHelperTest  extends TestJdwp {

	public VirtualMachineHelperTest() {
	}
	
	class MethodCallReferenceClass {
		public int add(Integer operand1, Integer operand2) {
			return operand1 + operand2;
		}
	}
	
	JdwpVerifier verifyMethodCall = new JdwpVerifier() {

		@Override
		protected void verifyOutsideOfSuT(Object... passedObjects) throws Throwable {

			// Prepare arguments
			DynamicElementInfo classInstance = (DynamicElementInfo) passedObjects[0];
			Value[] values = new Value[2];
			values[0] = JdwpObjectManager.getInstance().getObjectId((ElementInfo)passedObjects[1]); 
			values[1] = JdwpObjectManager.getInstance().getObjectId((ElementInfo)passedObjects[2]); 

			ClassInfo clazz = classInstance.getClassInfo();

			MethodInfo method = clazz.getMethod("add(Ljava/lang/Integer;Ljava/lang/Integer;)I", false);
			
			MethodResult mr = VirtualMachineHelper.invokeMethod(classInstance, method, values, VM.getVM().getCurrentThread(), 0);
			
			mr.write(dataOutputStream);
			
			// verify the results
			ByteBuffer bb = ByteBuffer.wrap(dataOutputBytes.toByteArray());

			/*
			 * Test that the header contains appropriate values
			 */
			assertEquals(Tag.INT.identifier().byteValue(), bb.get());
			assertEquals(6, bb.getInt());
		}

	};

	@Test
	public void simpleLocationTest() throws SecurityException, NoSuchFieldException {
		if (!isJPFRun()) {
			initialize(verifyMethodCall);
		}

		if (verifyNoPropertyViolation()) {

			MethodCallReferenceClass methodCallReferenceObject = new MethodCallReferenceClass();
			verifyMethodCall.verify(methodCallReferenceObject, new Integer(2), new Integer(4));
		}
	}

}
